package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

class DerivedValueTypeSchemaGenerator implements DerivedTypeSchemaGenerator {
  private static final String VALUE_INTERFACE_NAME = "Value";
  private final String name;

  public DerivedValueTypeSchemaGenerator(String name) {
    this.name = name;
  }

  @Override
  public StringBuilder getSchema() {
    StringBuilder builder = new StringBuilder();
    builder.append("type ").append(name).append(" implements ").append(VALUE_INTERFACE_NAME).append(" {\n")
           .append("  value: String!\n")
           .append("  type: String!\n")
           .append("}\n\n");

    return builder;
  }

}
