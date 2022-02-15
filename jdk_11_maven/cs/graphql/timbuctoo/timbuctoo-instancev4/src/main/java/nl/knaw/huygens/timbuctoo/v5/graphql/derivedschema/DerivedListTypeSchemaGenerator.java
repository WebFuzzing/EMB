package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.PaginationArgumentsHelper;

class DerivedListTypeSchemaGenerator implements DerivedTypeSchemaGenerator {
  private final String typeName;
  private final PaginationArgumentsHelper argumentsHelper;

  public DerivedListTypeSchemaGenerator(String typeName, PaginationArgumentsHelper argumentsHelper) {
    this.typeName = typeName;
    this.argumentsHelper = argumentsHelper;
  }

  @Override
  public StringBuilder getSchema() {
    StringBuilder builder = new StringBuilder();
    builder.append(argumentsHelper.makePaginatedListDefinition(typeName));
    return builder;
  }

}
