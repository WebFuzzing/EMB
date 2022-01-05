package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.google.common.collect.Maps;
import graphql.TypeResolutionEnvironment;
import graphql.language.BooleanValue;
import graphql.language.Directive;
import graphql.language.EnumValue;
import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.TypeResolver;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.InterfaceWiringEnvironment;
import graphql.schema.idl.UnionWiringEnvironment;
import graphql.schema.idl.WiringFactory;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.CollectionDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.DynamicRelationDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.QuadStoreLookUpSubjectByUriFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.RelationDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.RelationsOfSubjectDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.TypedLiteralDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.UnionDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.TypedValue;
import nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration.DefaultSummaryProps;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.CreateMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.DefaultIndexConfigMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.DeleteMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.EditMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.PersistEntityMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.SetCustomProvenanceMutation;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.valueOf;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.UNKNOWN;

public class RdfWiringFactory implements WiringFactory {
  private final UriFetcher uriFetcher;
  private final LookUpSubjectByUriFetcherWrapper lookupFetcher;
  private final ObjectTypeResolver objectTypeResolver;
  private final DataSetRepository dataSetRepository;
  private final PaginationArgumentsHelper argumentsHelper;
  private final UriHelper uriHelper;
  private final RedirectionService redirectionService;
  private final Runnable schemaUpdater;
  private final EntityTitleFetcher entityTitleFetcher;
  private final EntityDescriptionFetcher entityDescriptionFetcher;
  private final EntityImageFetcher entityImageFetcher;
  private final OtherDataSetFetcher otherDataSetFetcher;
  private final QuadStoreLookUpSubjectByUriFetcher subjectFetcher;
  private final Map<String, CreateMutation> createMutationMap = Maps.newHashMap();
  private final Map<String, EditMutation> editMutationMap = Maps.newHashMap();
  private final Map<String, DeleteMutation> deleteMutationMap = Maps.newHashMap();
  private final DynamicRelationDataFetcher dynamicRelationDataFetcher;

  public RdfWiringFactory(DataSetRepository dataSetRepository, PaginationArgumentsHelper argumentsHelper,
                          DefaultSummaryProps defaultSummaryProps, UriHelper uriHelper,
                          RedirectionService redirectionService,
                          Runnable schemaUpdater) {
    this.dataSetRepository = dataSetRepository;
    this.argumentsHelper = argumentsHelper;
    this.uriHelper = uriHelper;
    this.redirectionService = redirectionService;
    this.schemaUpdater = schemaUpdater;
    objectTypeResolver = new ObjectTypeResolver();
    uriFetcher = new UriFetcher();
    subjectFetcher = new QuadStoreLookUpSubjectByUriFetcher();
    lookupFetcher = new LookUpSubjectByUriFetcherWrapper("uri", subjectFetcher);
    entityTitleFetcher = new EntityTitleFetcher(defaultSummaryProps.getDefaultTitles());
    entityDescriptionFetcher = new EntityDescriptionFetcher(defaultSummaryProps.getDefaultDescriptions());
    entityImageFetcher = new EntityImageFetcher(defaultSummaryProps.getDefaultImages());
    otherDataSetFetcher = new OtherDataSetFetcher(dataSetRepository);
    dynamicRelationDataFetcher = new DynamicRelationDataFetcher(argumentsHelper);
  }

  @Override
  public boolean providesTypeResolver(InterfaceWiringEnvironment environment) {
    return true;
  }

  @Override
  public boolean providesTypeResolver(UnionWiringEnvironment environment) {
    return true;
  }

  @Override
  public TypeResolver getTypeResolver(InterfaceWiringEnvironment environment) {
    return objectTypeResolver;
  }

  @Override
  public TypeResolver getTypeResolver(UnionWiringEnvironment environment) {
    return objectTypeResolver;
  }

  @Override
  public boolean providesDataFetcher(FieldWiringEnvironment environment) {
    return environment.getFieldDefinition().getDirective("fromCollection") != null ||
      environment.getFieldDefinition().getDirective("lookupUri") != null ||
      environment.getFieldDefinition().getDirective("rdf") != null ||
      environment.getFieldDefinition().getDirective("uri") != null ||
      environment.getFieldDefinition().getDirective("passThrough") != null ||
      environment.getFieldDefinition().getDirective("related") != null ||
      environment.getFieldDefinition().getDirective("dataSet") != null ||
      environment.getFieldDefinition().getDirective("entityTitle") != null ||
      environment.getFieldDefinition().getDirective("entityDescription") != null ||
      environment.getFieldDefinition().getDirective("entityImage") != null ||
      environment.getFieldDefinition().getDirective("otherDataSets") != null ||
      environment.getFieldDefinition().getDirective("getAllOfPredicate") != null ||
      environment.getFieldDefinition().getDirective("createMutation") != null ||
      environment.getFieldDefinition().getDirective("editMutation") != null ||
      environment.getFieldDefinition().getDirective("deleteMutation") != null ||
      environment.getFieldDefinition().getDirective("persistEntityMutation") != null ||
      environment.getFieldDefinition().getDirective("setCustomProvenanceMutation") != null ||
      environment.getFieldDefinition().getDirective("resetIndex") != null;
  }

  @Override
  public DataFetcher getDataFetcher(FieldWiringEnvironment environment) {
    if (environment.getFieldDefinition().getDirective("passThrough") != null) {
      return DataFetchingEnvironment::getSource;
    } else if (environment.getFieldDefinition().getDirective("related") != null) {
      final Directive directive = environment.getFieldDefinition().getDirective("related");
      String source = ((StringValue) directive.getArgument("source").getValue()).getValue();
      String predicate = ((StringValue) directive.getArgument("predicate").getValue()).getValue();
      String direction = ((StringValue) directive.getArgument("direction").getValue()).getValue();
      return new CollectionFetcherWrapper(argumentsHelper, new RelationsOfSubjectDataFetcher(
        source,
        predicate,
        Direction.valueOf(direction)
      ));
    } else if (environment.getFieldDefinition().getDirective("lookupUri") != null) {
      return lookupFetcher;
    } else if (environment.getFieldDefinition().getDirective("fromCollection") != null) {
      final Directive directive = environment.getFieldDefinition().getDirective("fromCollection");
      String uri = ((StringValue) directive.getArgument("uri").getValue()).getValue();
      return new CollectionFetcherWrapper(argumentsHelper, new CollectionDataFetcher(uri));
    } else if (environment.getFieldDefinition().getDirective("rdf") != null) {
      final Directive directive = environment.getFieldDefinition().getDirective("rdf");
      String uri = ((StringValue) directive.getArgument("predicate").getValue()).getValue();
      Direction direction = valueOf(((StringValue) directive.getArgument("direction").getValue()).getValue());
      boolean isList = ((BooleanValue) directive.getArgument("isList").getValue()).isValue();
      boolean isObject = ((BooleanValue) directive.getArgument("isObject").getValue()).isValue();
      boolean isValue = ((BooleanValue) directive.getArgument("isValue").getValue()).isValue();
      if (isObject && isValue) {
        return new DataFetcherWrapper(argumentsHelper, isList, new UnionDataFetcher(uri, direction));
      } else {
        if (isObject) {
          return new DataFetcherWrapper(argumentsHelper, isList, new RelationDataFetcher(uri, direction));
        } else {
          return new DataFetcherWrapper(argumentsHelper, isList, new TypedLiteralDataFetcher(uri));
        }
      }
    } else if (environment.getFieldDefinition().getDirective("uri") != null) {
      return uriFetcher;
    } else if (environment.getFieldDefinition().getDirective("dataSet") != null) {
      final Directive directive = environment.getFieldDefinition().getDirective("dataSet");
      String userId = ((StringValue) directive.getArgument("userId").getValue()).getValue();
      String dataSetId = ((StringValue) directive.getArgument("dataSetId").getValue()).getValue();
      final DataSet dataSet = dataSetRepository.unsafeGetDataSetWithoutCheckingPermissions(userId, dataSetId)
                                               .orElse(null);
      return dataFetchingEnvironment -> new DatabaseResult() {
        @Override
        public DataSet getDataSet() {
          return dataSet;
        }
      };
    } else if (environment.getFieldDefinition().getDirective("entityTitle") != null) {
      return entityTitleFetcher;
    } else if (environment.getFieldDefinition().getDirective("entityDescription") != null) {
      return entityDescriptionFetcher;
    } else if (environment.getFieldDefinition().getDirective("entityImage") != null) {
      return entityImageFetcher;
    } else if (environment.getFieldDefinition().getDirective("otherDataSets") != null) {
      return otherDataSetFetcher;
    } else if (environment.getFieldDefinition().getDirective("getAllOfPredicate") != null) {
      return dynamicRelationDataFetcher;
    } else if (environment.getFieldDefinition().getDirective("createMutation") != null) {
      Directive directive = environment.getFieldDefinition().getDirective("createMutation");
      StringValue dataSet = (StringValue) directive.getArgument("dataSet").getValue();
      StringValue typeUri = (StringValue) directive.getArgument("typeUri").getValue();

      String dataSetName = dataSet.getValue();
      String typeUriName = typeUri.getValue();

      return createMutationMap.computeIfAbsent(dataSetName + '\n' + typeUriName, s -> new CreateMutation(
        schemaUpdater,
        dataSetRepository,
        uriHelper,
        subjectFetcher,
        dataSetName,
        typeUriName
      ));
    } else if (environment.getFieldDefinition().getDirective("editMutation") != null) {
      Directive directive = environment.getFieldDefinition().getDirective("editMutation");
      StringValue dataSet = (StringValue) directive.getArgument("dataSet").getValue();

      String dataSetName = dataSet.getValue();

      return editMutationMap.computeIfAbsent(dataSetName, s -> {
        return new EditMutation(schemaUpdater, dataSetRepository, uriHelper, subjectFetcher, dataSetName);
      });
    } else if (environment.getFieldDefinition().getDirective("deleteMutation") != null) {
      Directive directive = environment.getFieldDefinition().getDirective("deleteMutation");
      StringValue dataSet = (StringValue) directive.getArgument("dataSet").getValue();

      String dataSetName = dataSet.getValue();

      return deleteMutationMap.computeIfAbsent(dataSetName, s -> {
        return new DeleteMutation(schemaUpdater, dataSetRepository, uriHelper, dataSetName);
      });
    } else if (environment.getFieldDefinition().getDirective("persistEntityMutation") != null) {
      Directive directive = environment.getFieldDefinition().getDirective("persistEntityMutation");
      StringValue dataSet = (StringValue) directive.getArgument("dataSet").getValue();
      String dataSetName = dataSet.getValue();
      return new PersistEntityMutation(schemaUpdater, redirectionService, dataSetName, uriHelper);
    } else if (environment.getFieldDefinition().getDirective("setCustomProvenanceMutation") != null) {
      Directive directive = environment.getFieldDefinition().getDirective("setCustomProvenanceMutation");
      StringValue dataSet = (StringValue) directive.getArgument("dataSet").getValue();
      String dataSetId = dataSet.getValue();
      return new SetCustomProvenanceMutation(schemaUpdater, dataSetRepository, dataSetId);
    } else if (environment.getFieldDefinition().getDirective("resetIndex") != null) {
      final Directive resetIndex = environment.getFieldDefinition().getDirective("resetIndex");
      final StringValue dataSet = (StringValue) resetIndex.getArgument("dataSet").getValue();
      return new DefaultIndexConfigMutation(schemaUpdater, dataSetRepository, dataSet.getValue());
    }
    return null;
  }

  @Override
  public DataFetcher getDefaultDataFetcher(FieldWiringEnvironment environment) {
    return new PropertyDataFetcher(environment.getFieldDefinition().getName());
  }

  private static class ObjectTypeResolver implements TypeResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectTypeResolver.class);

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment environment) {

      String typeName;
      Object object = environment.getObject();
      if (object instanceof TypedValue) {
        final TypedValue typedValue = (TypedValue) object;
        final String typeUri = typedValue.getType();
        final String prefix = typedValue.getDataSet().getMetadata().getCombinedId();
        typeName =
          prefix +
            "_" +
            typedValue.getDataSet().getTypeNameStore().makeGraphQlValuename(typeUri);
      } else if (object instanceof SubjectReference) {
        //Often a thing has one type. In that case this lambda is easy to implement. Simply return that type
        //In rdf things can have more then one type though (types are like java interfaces)
        //Since this lambda only allows us to return 1 type we need to do a bit more work and return one of the types
        //that the user actually requested
        final SubjectReference subjectReference = (SubjectReference) object;
        final String prefix = subjectReference.getDataSet().getMetadata().getCombinedId();
        Set<String> typeUris = subjectReference.getTypes();
        final TypeNameStore typeNameStore = subjectReference.getDataSet().getTypeNameStore();
        if (typeUris.isEmpty()) {
          typeName = prefix + "_" + typeNameStore.makeGraphQlname(UNKNOWN);
        } else {
          typeName = null;
          for (Selection selection : environment.getField().getSelectionSet().getSelections()) {
            if (selection instanceof InlineFragment) {
              InlineFragment fragment = (InlineFragment) selection;
              final String typeConditionName = fragment.getTypeCondition().getName();
              String typeUri = typeNameStore.makeUri(
                typeConditionName.startsWith(prefix) ?
                  typeConditionName.substring(prefix.length() + 1) :
                  typeConditionName
              );
              if (typeUris.contains(typeUri)) {
                typeName = prefix + "_" + typeNameStore.makeGraphQlname(typeUri);
                break;
              }
            } else if (selection instanceof Field && ((Field) selection).getName().equals("__typename")) {
              //Ignore, __typename is indeed not part of a fragment
            } else {
              //The selection on the interface is not an InlineFragment. I.e. they query the interface on the interface
              //itself. This is no problem. The code below picks a random type which will implement the interface,
              //guaranteed
            }
          }
          if (typeName == null) {
            //there's no overlap. Just pick one (object will be excluded from the result)
            typeName = prefix + "_" + typeNameStore.makeGraphQlname(
              typeUris.isEmpty() ? UNKNOWN : typeUris.iterator().next()
            );
          }
        }
      } else {
        throw new RuntimeException("Expected either a 'TypedValue' or a 'SubjectReference', but was: " +
          (object == null ? "null" : object.getClass()));
      }
      final GraphQLObjectType type = (GraphQLObjectType) environment.getSchema().getType(typeName);
      return type;
    }
  }
}
