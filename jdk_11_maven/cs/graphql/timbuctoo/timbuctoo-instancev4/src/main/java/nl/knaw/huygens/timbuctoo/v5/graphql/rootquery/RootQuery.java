package nl.knaw.huygens.timbuctoo.v5.graphql.rootquery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import nl.knaw.huygens.timbuctoo.remote.rs.ResourceSyncService;
import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncFileLoader;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitType;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.SupportedExportFormats;
import nl.knaw.huygens.timbuctoo.v5.graphql.customschema.MergeSchemas;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.DataMetaDataListFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.DataSetImportStatusFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.DiscoverResourceSyncDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.ImportStatusFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.RdfWiringFactory;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.SummaryPropertiesDataFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.RootData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema.DerivedSchemaContainer;
import nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema.DerivedSchemaGenerator;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.CollectionMetadataMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.CreateDataSetMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.DataSetMetadataMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.DeleteDataSetMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ExtendSchemaMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.IndexConfigMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.MakePublicMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ResourceSyncImportMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ResourceSyncUpdateMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.SummaryPropsMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ViewConfigMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.CollectionMetadata;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.CollectionMetadataList;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutableCollectionMetadata;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutableCollectionMetadataList;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutablePrefixMapping;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutableProperty;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutablePropertyList;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ImmutableStringList;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.MimeTypeDescription;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.Property;
import nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders.ViewConfigFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.io.Resources.getResource;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HASINDEXERCONFIG;

public class RootQuery implements Supplier<GraphQLSchema> {

  private static final Logger LOG = LoggerFactory.getLogger(RootQuery.class);


  private final DataSetRepository dataSetRepository;
  private final SupportedExportFormats supportedFormats;
  private final String archetypes;
  private final RdfWiringFactory wiringFactory;
  private final DerivedSchemaGenerator typeGenerator;
  private final ObjectMapper objectMapper;
  private final ResourceSyncFileLoader resourceSyncFileLoader;
  private final ResourceSyncService resourceSyncService;
  private final ExecutorService schemaAccessQueue;
  private final SchemaParser schemaParser;
  private final String staticQuery;
  private GraphQLSchema graphQlSchema;

  public RootQuery(DataSetRepository dataSetRepository, SupportedExportFormats supportedFormats,
                   String archetypes, Function<Runnable, RdfWiringFactory> wiringFactory,
                   DerivedSchemaGenerator typeGenerator, ObjectMapper objectMapper,
                   ResourceSyncFileLoader resourceSyncFileLoader, ResourceSyncService resourceSyncService,
                   ExecutorService schemaAccessQueue)
    throws IOException {
    this.dataSetRepository = dataSetRepository;
    this.supportedFormats = supportedFormats;
    this.archetypes = archetypes;
    this.wiringFactory = wiringFactory.apply(this::scheduleRebuild);
    this.typeGenerator = typeGenerator;
    this.objectMapper = objectMapper;
    this.resourceSyncFileLoader = resourceSyncFileLoader;
    this.resourceSyncService = resourceSyncService;
    this.schemaAccessQueue = schemaAccessQueue;
    staticQuery = Resources.toString(getResource(RootQuery.class, "schema.graphql"), Charsets.UTF_8);
    schemaParser = new SchemaParser();
    dataSetRepository.subscribeToDataSetsUpdated(this::scheduleRebuild);
  }

  private void scheduleRebuild() {
    schemaAccessQueue.submit(this::rebuildSchema);
  }

  private void rebuildSchema() {
    try {
      graphQlSchema = this.generateSchema();
    } catch (Exception | Error e) {
      LOG.error("Schema generation error", e);
      throw e;
    }
  }

  private GraphQLSchema generateSchema() {
    final TypeDefinitionRegistry staticQuery = schemaParser.parse(this.staticQuery);
    if (archetypes != null && !archetypes.isEmpty()) {
      staticQuery.merge(schemaParser.parse(
        archetypes +
          "extend type DataSetMetadata {\n" +
          "  archetypes: Archetypes! @passThrough\n" +
          "}\n" +
          "\n")
      );
    }
    TypeDefinitionRegistry registry = new TypeDefinitionRegistry();
    registry.merge(staticQuery);

    final RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();

    wiring.type("Query", builder -> builder
      .dataFetcher("discoverResourceSync", new DiscoverResourceSyncDataFetcher(resourceSyncService))
      .dataFetcher("promotedDataSets", env -> dataSetRepository.getPromotedDataSets()
        .stream()
        .map(dataSet -> {
          ContextData contextData = env.getContext();
          UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
          return new DataSetWithDatabase(dataSet, userPermissionCheck);
        })
        .collect(Collectors.toList()))
      .dataFetcher("allDataSets", env -> dataSetRepository.getDataSets()
        .stream()
        .map(dataSet -> {
          ContextData contextData = env.getContext();
          UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
          return new DataSetWithDatabase(dataSet, userPermissionCheck);
        })
        .filter(x -> {
          if (x.isPublished()) {
            return true;
          } else {
            ContextData contextData = env.getContext();
            UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
            return userPermissionCheck.hasPermission(x.getDataSet().getMetadata(), Permission.READ);
          }
        })
        .collect(Collectors.toList()))
      .dataFetcher("dataSetMetadata", env -> {
        final String dataSetId = env.getArgument("dataSetId");
        ContextData context = env.getContext();
        final User user = context.getUser().orElse(null);

        Tuple<String, String> splitCombinedId = DataSetMetaData.splitCombinedId(dataSetId);

        return dataSetRepository.getDataSet(user, splitCombinedId.getLeft(), splitCombinedId.getRight())
                                .map(dataSet -> {
                                  ContextData contextData = env.getContext();
                                  UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
                                  return new DataSetWithDatabase(dataSet, userPermissionCheck);
                                });
      })
      .dataFetcher("dataSetMetadataList", env -> {
        UserPermissionCheck userPermissionCheck = ((ContextData) env.getContext()).getUserPermissionCheck();
        Stream<DataSetWithDatabase> dataSets = dataSetRepository.getDataSets()
          .stream()
          .map(dataSet -> new DataSetWithDatabase(dataSet, userPermissionCheck));
        if (env.getArgument("promotedOnly")) {
          dataSets = dataSets.filter(DataSetWithDatabase::isPromoted);
        }
        if (env.getArgument("publishedOnly")) {
          dataSets = dataSets.filter(DataSetWithDatabase::isPublished);
        }
        return dataSets
          .filter(x -> userPermissionCheck.hasPermission(x.getDataSet().getMetadata(), Permission.READ))
          .collect(Collectors.toList());
      })
      .dataFetcher("aboutMe", env -> ((RootData) env.getRoot()).getCurrentUser().orElse(null))
      .dataFetcher("availableExportMimetypes", env -> supportedFormats.getSupportedMimeTypes().stream()
        .map(MimeTypeDescription::create)
        .collect(Collectors.toList())
      )
    );
    wiring.type("DataSetMetadata", builder -> builder
      .dataFetcher("dataSetImportStatus", new DataSetImportStatusFetcher(dataSetRepository))
      .dataFetcher("importStatus", new ImportStatusFetcher(dataSetRepository))
      .dataFetcher("prefixMappings", env -> {
        DataSetMetaData input = env.getSource();
        ContextData context = env.getContext();
        final User user = context.getUser().orElse(null);
        final DataSet dataSet = dataSetRepository.getDataSet(user, input.getOwnerId(), input.getDataSetId()).get();
        final TypeNameStore typeNameStore = dataSet.getTypeNameStore();
        return typeNameStore.getMappings().entrySet().stream().map(mappingEntry ->
                ImmutablePrefixMapping
                        .builder()
                        .prefix(mappingEntry.getKey())
                        .uri(mappingEntry.getValue())
                        .build()
        ).collect(Collectors.toList());
      })
      .dataFetcher("collectionList", env -> getCollections(env.getSource(), ((ContextData) env.getContext()).getUser()))
      .dataFetcher("collection", env -> {
        String collectionId = (String) env.getArguments().get("collectionId");
        if (collectionId != null && collectionId.endsWith("List")) {
          collectionId = collectionId.substring(0, collectionId.length() - "List".length());
        }
        DataSetMetaData input = env.getSource();
        ContextData context = env.getContext();
        final User user = context.getUser().orElse(null);

        final DataSet dataSet = dataSetRepository.getDataSet(user, input.getOwnerId(), input.getDataSetId()).get();
        final TypeNameStore typeNameStore = dataSet.getTypeNameStore();
        String collectionUri = typeNameStore.makeUri(collectionId);
        if (dataSet.getSchemaStore().getStableTypes() == null ||
          dataSet.getSchemaStore().getStableTypes().get(collectionUri) == null) {
          return null;
        } else {
          return getCollection(dataSet, typeNameStore, dataSet.getSchemaStore().getStableTypes().get(collectionUri));
        }
      })
      .dataFetcher("dataSetId", env -> ((DataSetMetaData) env.getSource()).getCombinedId())
      .dataFetcher("dataSetName", env -> ((DataSetMetaData) env.getSource()).getDataSetId())
      .dataFetcher("ownerId", env -> ((DataSetMetaData) env.getSource()).getOwnerId())
      .dataFetcher("userPermissions", env -> ((DataSetWithDatabase) env.getSource()).getUserPermissions())
    );

    wiring.type("CollectionMetadata", builder -> builder
      .dataFetcher("indexConfig", env -> {
        SubjectReference source = env.getSource();
        final QuadStore qs = source.getDataSet().getQuadStore();
        try (Stream<CursorQuad> quads = qs.getQuads(source.getSubjectUri(), TIM_HASINDEXERCONFIG, Direction.OUT, "")) {
          final Map result = quads.findFirst()
            .map(q -> {
              try {
                return objectMapper.readValue(q.getObject(), Map.class);
              } catch (IOException e) {
                LOG.error("Value not a Map", e);
                return new HashMap<>();
              }
            })
            .orElse(new HashMap());
          if (!result.containsKey("facet") || !(result.get("facet") instanceof List)) {
            result.put("facet", new ArrayList<>());
          }
          if (!result.containsKey("fullText") || !(result.get("fullText") instanceof List)) {
            result.put("fullText", new ArrayList<>());
          }
          return result;
        }
      })
      .dataFetcher("viewConfig", new ViewConfigFetcher(objectMapper))
      .dataFetcher("summaryProperties", new SummaryPropertiesDataFetcher(objectMapper))
    );

    wiring.type("AboutMe", builder -> builder
      .dataFetcher("dataSets", env -> (Iterable) () -> dataSetRepository
        .getDataSetsWithWriteAccess(env.getSource())
        .stream().map(dataSet -> {
          ContextData contextData = env.getContext();
          UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
          return new DataSetWithDatabase(dataSet, userPermissionCheck);
        }).iterator()
      )
      .dataFetcher("dataSetMetadataList", new DataMetaDataListFetcher(dataSetRepository))
      .dataFetcher("id", env -> ((User) env.getSource()).getPersistentId())
      .dataFetcher("name", env -> ((User) env.getSource()).getDisplayName())
      .dataFetcher("personalInfo", env -> "http://example.com")
      .dataFetcher("canCreateDataSet", env -> true)
    );

    wiring.type("Mutation", builder -> builder
      .dataFetcher("setViewConfig", new ViewConfigMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("setSummaryProperties", new SummaryPropsMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("setIndexConfig", new IndexConfigMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("createDataSet", new CreateDataSetMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("deleteDataSet", new DeleteDataSetMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("publish", new MakePublicMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("extendSchema", new ExtendSchemaMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("setDataSetMetadata",
        new DataSetMetadataMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("setCollectionMetadata",
        new CollectionMetadataMutation(this::scheduleRebuild, dataSetRepository))
      .dataFetcher("resourceSyncImport",
        new ResourceSyncImportMutation(this::scheduleRebuild, dataSetRepository, resourceSyncFileLoader))
      .dataFetcher("resourceSyncUpdate",
        new ResourceSyncUpdateMutation(this::scheduleRebuild, dataSetRepository, resourceSyncFileLoader))
    );

    wiring.wiringFactory(wiringFactory);
    StringBuilder rootQuery = new StringBuilder("type DataSets {\n sillyWorkaroundWhenNoDataSetsAreVisible: Boolean\n");
    StringBuilder rootMut = new StringBuilder("type DataSetMutations {\n")
      .append("  sillyWorkaroundWhenNoMutationsAreAllowed: Boolean\n");

    boolean[] dataSetAvailable = new boolean[]{false};
    boolean[] hasTypes = new boolean[]{false};

    // add data sets query to the schema
    dataSetRepository.getDataSets().forEach(dataSet -> {
      final DataSetMetaData dataSetMetaData = dataSet.getMetadata();
      final String name = dataSetMetaData.getCombinedId();


      Map<String, Type> types = dataSet.getSchemaStore().getStableTypes();

      Map<String, List<ExplicitField>> customSchema = dataSet.getCustomSchema();

      final Map<String, Type> customTypes = new HashMap<>();

      for (Map.Entry<String, List<ExplicitField>> entry : customSchema.entrySet()) {
        ExplicitType explicitType = new ExplicitType(entry.getKey(), entry.getValue());
        customTypes.put(entry.getKey(), explicitType.convertToType());
      }

      Map<String, Type> mergedTypes;

      MergeSchemas mergeSchemas = new MergeSchemas();
      mergedTypes = mergeSchemas.mergeSchema(types, customTypes);

      types = mergedTypes;


      if (types != null) {

        // Add to rootQuery
        dataSetAvailable[0] = true;
        rootQuery.append("  ")
          .append(name)
          .append(":")
          .append(name)
          .append(" @dataSet(userId:\"")
          .append(dataSetMetaData.getOwnerId())
          .append("\", dataSetId:\"")
          .append(dataSetMetaData.getDataSetId())
          .append("\")\n");

        wiring.type(name, c -> c
          .dataFetcher("metadata", env ->
              new DataSetWithDatabase(dataSet, env.<ContextData>getContext().getUserPermissionCheck()))
        );

        final DerivedSchemaContainer schema = typeGenerator.makeGraphQlTypes(
          name,
          types,
          dataSet.getTypeNameStore(),
          dataSet.getReadOnlyChecker(),
          dataSet.getCustomProvenance()
        );

        if (schema.hasMutationTypes()) {
          hasTypes[0] = true;
          rootMut.append("  ")
                 .append(name)
                 .append(": ")
                 .append(name).append("Mutations")
                 .append(" @passThrough")
                 .append("\n\n");
        }

        staticQuery.merge(schemaParser.parse(schema.getSchema()));
      }
    });
    rootQuery.append("}\n\nextend type Query {\n  #The actual dataSets\n  dataSets: DataSets @passThrough\n}\n\n");
    rootMut.append("}\n\nextend type Mutation {\n  #The actual dataSets\n" +
      "  dataSets: DataSetMutations @passThrough\n}\n\n");

    if (dataSetAvailable[0]) {
      staticQuery.merge(schemaParser.parse(rootQuery.toString()));
      if (hasTypes[0]) {
        staticQuery.merge(schemaParser.parse(rootMut.toString()));
      }
    }

    return new SchemaGenerator().makeExecutableSchema(staticQuery, wiring.build());
  }

  public CollectionMetadataList getCollections(DataSetMetaData input, Optional<User> userOpt) {
    final User user = userOpt.orElse(null);
    final DataSet dataSet = dataSetRepository.getDataSet(user, input.getOwnerId(), input.getDataSetId()).get();

    final TypeNameStore typeNameStore = dataSet.getTypeNameStore();
    final List<CollectionMetadata> colls = dataSet
      .getSchemaStore()
      .getStableTypes().values().stream()
      .map(x -> {
        return getCollection(dataSet, typeNameStore, x);
      })
      .collect(Collectors.toList());
    return ImmutableCollectionMetadataList.builder()
      .nextCursor(Optional.empty())
      .prevCursor(Optional.empty())
      .items(colls)
      .build();
  }

  public CollectionMetadata getCollection(DataSet dataSet, TypeNameStore typeNameStore, Type collectionType) {
    final long occurrences = collectionType.getSubjectsWithThisType();
    final String collectionId = typeNameStore.makeGraphQlname(collectionType.getName());
    final String fullyQualifiedTypeName = dataSet.getMetadata().getCombinedId() + "_" + collectionId;
    return ImmutableCollectionMetadata.builder()
      .subjectUri(collectionType.getName())
      .shortenedUri(typeNameStore.shorten(collectionType.getName()))
      .types(Collections.emptySet())
      .dataSet(dataSet)
      .collectionId(collectionId)
      .collectionListId(collectionId + "List")
      .itemType(fullyQualifiedTypeName)
      .total(occurrences)
      .properties(ImmutablePropertyList.builder()
        .prevCursor(Optional.empty())
        .nextCursor(Optional.empty())
        .items(() -> collectionType.getPredicates().stream().map(pred -> {
            return (Property) ImmutableProperty.builder()
              .density(getDensity(occurrences, pred.getSubjectsWithThisPredicate()))
              .isList(pred.isList())
              .uri(pred.getName())
              .shortenedUri(typeNameStore.shorten(pred.getName()))
              .isInverse(pred.getDirection() == Direction.IN)
              .name(typeNameStore.makeGraphQlnameForPredicate(
                pred.getName(), pred.getDirection(), pred.isList())
              )
              .referencedCollections(ImmutableStringList.builder()
                .prevCursor(Optional.empty())
                .nextCursor(Optional.empty())
                .items(() -> pred.getUsedReferenceTypes().stream()
                  .map(typeNameStore::makeGraphQlname)
                  .iterator())
                .build()
              )
              .isValueType(!pred.getValueTypes().isEmpty())
              .build();
          }
        ).iterator())
        .build())
      .build();
  }

  public long getDensity(long allOccurrences, long predOccurrences) {
    final long percentage = allOccurrences > 0 ? (predOccurrences * 100) / allOccurrences : 0;
    if (percentage == 0 && predOccurrences > 0) {
      return 1;//don't return 0 unless it's actually an empty predicate
    } else if (percentage == 100 && predOccurrences < allOccurrences) {
      return 99;//don't return 100 unless it's actually a 100%
    } else {
      return percentage;
    }
  }

  @Override
  public GraphQLSchema get() {
    if (graphQlSchema == null) {
      this.scheduleRebuild();
    }
    try {
      return schemaAccessQueue.submit(() -> graphQlSchema).get();
    } catch (InterruptedException | ExecutionException e) {
      LOG.error("Unable to read schema", e);
      throw new RuntimeException("Unable to read schema");
    }
  }

}
