package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;

import java.util.Set;

public interface DerivedObjectTypeSchemaGenerator extends DerivedTypeSchemaGenerator {

  void objectField(String description, Predicate predicate, String typeUri);

  void unionField(String description, Predicate predicate, Set<String> typeUris);

  void valueField(String description, Predicate predicate, String typeUri);

  @Override
  StringBuilder getSchema();

  void addMutationToSchema(StringBuilder schema);

  void addQueryToSchema(StringBuilder schema);

}
