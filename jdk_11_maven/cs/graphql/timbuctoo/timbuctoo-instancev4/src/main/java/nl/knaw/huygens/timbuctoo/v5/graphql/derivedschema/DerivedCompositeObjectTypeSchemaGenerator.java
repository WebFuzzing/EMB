package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;

import java.util.Set;

public class DerivedCompositeObjectTypeSchemaGenerator implements DerivedObjectTypeSchemaGenerator {

  private final DerivedQueryObjectTypeSchemaGenerator querySchemaGenerator;
  private final DerivedInputTypeSchemaGenerator mutationSchemaGenerator;

  public DerivedCompositeObjectTypeSchemaGenerator(String typeUri, String rootType,
                                                   GraphQlNameGenerator nameStore,
                                                   DerivedSchemaContainer derivedSchemaContainer,
                                                   ReadOnlyChecker readOnlyChecker, CustomProvenance customProvenance) {
    querySchemaGenerator =
      new DerivedQueryObjectTypeSchemaGenerator(typeUri, rootType, nameStore, derivedSchemaContainer);
    mutationSchemaGenerator =
      new DerivedInputTypeSchemaGenerator(typeUri, rootType, nameStore, derivedSchemaContainer, readOnlyChecker,
        customProvenance);
  }

  @Override
  public void objectField(String description, Predicate predicate, String typeUri) {
    querySchemaGenerator.objectField(description, predicate, typeUri);
    mutationSchemaGenerator.objectField(description, predicate, typeUri);

  }

  @Override
  public void unionField(String description, Predicate predicate, Set<String> typeUris) {
    querySchemaGenerator.unionField(description, predicate, typeUris);
    mutationSchemaGenerator.unionField(description, predicate, typeUris);
  }

  @Override
  public void valueField(String description, Predicate predicate, String typeUri) {
    querySchemaGenerator.valueField(description, predicate, typeUri);
    mutationSchemaGenerator.valueField(description, predicate, typeUri);
  }

  @Override
  public StringBuilder getSchema() {
    StringBuilder schema = new StringBuilder();

    schema.append(querySchemaGenerator.getSchema()).append("\n");
    schema.append(mutationSchemaGenerator.getSchema()).append("\n");

    return schema;
  }

  @Override
  public void addMutationToSchema(StringBuilder schema) {
    mutationSchemaGenerator.addMutationToSchema(schema);
  }

  @Override
  public void addQueryToSchema(StringBuilder schema) {
    querySchemaGenerator.addQueryToSchema(schema);
  }
}
