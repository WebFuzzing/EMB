package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.PaginationArgumentsHelper;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DerivedSchemaGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(DerivedSchemaGenerator.class);
  private final PaginationArgumentsHelper argumentsHelper;

  public DerivedSchemaGenerator(
    PaginationArgumentsHelper argumentsHelper) {
    this.argumentsHelper = argumentsHelper;
  }

  public DerivedSchemaContainer makeGraphQlTypes(String rootType, Map<String, Type> types, TypeNameStore nameStore,
                                                 ReadOnlyChecker readOnlyChecker, CustomProvenance customProvenance) {
    GraphQlNameGenerator nameGenerator = new GraphQlNameGenerator(nameStore);

    DerivedSchemaContainer typesContainer = new DerivedSchemaContainer(rootType, nameGenerator, this.argumentsHelper,
      readOnlyChecker, customProvenance);

    // FIXME find a better way to register standard types to the schema of a data set
    typesContainer.valueType(RdfConstants.MARKDOWN);
    typesContainer.valueType(RdfConstants.STRING);
    typesContainer.valueType(RdfConstants.URI);

    for (Type type : types.values()) {
      DerivedObjectTypeSchemaGenerator typeSchemaGenerator = typesContainer.addObjectType(type.getName());
      for (Predicate predicate : type.getPredicates()) {
        fieldForDerivedType(predicate, typesContainer, typeSchemaGenerator, nameGenerator, rootType);
      }
    }
    return typesContainer;
  }

  private static void fieldForDerivedType(Predicate pred, DerivedSchemaContainer typesContainer,
                                          DerivedObjectTypeSchemaGenerator typeSchemaGenerator,
                                          GraphQlNameGenerator nameGenerator, String rootType) {
    if (pred.getReferenceTypes().size() == 0) {
      if (pred.getValueTypes().size() == 0) {
        LOG.error(
          "This shouldn't happen! The predicate '{}' has no value types and no reference types!",
          pred.getName()
        );
      } else if (pred.getValueTypes().size() == 1) {
        typeSchemaGenerator.valueField(
          null,
          pred,
          pred.getUsedValueTypes().iterator().next()
        );
      } else {
        Set<String> types = new HashSet<>();
        for (String valueType : pred.getUsedValueTypes()) {
          types.add(typesContainer.valueType(valueType));
        }
        typeSchemaGenerator.unionField(null, pred, types);
      }
    } else {
      if (pred.getReferenceTypes().size() == 1 && pred.getValueTypes().size() == 0) {
        typeSchemaGenerator.objectField(
          null,
          pred,
          nameGenerator.createObjectTypeName(rootType, pred.getUsedReferenceTypes().iterator().next())
        );
      } else {
        Set<String> refs = new HashSet<>();
        for (String referenceType : pred.getUsedReferenceTypes()) {
          refs.add(nameGenerator.createObjectTypeName(rootType, referenceType));
        }
        for (String valueType : pred.getUsedValueTypes()) {
          refs.add(typesContainer.valueType(valueType));
        }

        typeSchemaGenerator.unionField(
          null,
          pred,
          refs
        );
      }
    }
  }


}
