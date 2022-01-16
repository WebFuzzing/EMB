package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import java.util.Set;

class DerivedUnionTypeSchemaGenerator implements DerivedTypeSchemaGenerator {
  private final String unionName;
  private final Set<String> refs;

  public DerivedUnionTypeSchemaGenerator(String unionName, Set<String> refs) {
    this.unionName = unionName;
    this.refs = refs;
  }

  @Override
  public StringBuilder getSchema() {
    StringBuilder builder = new StringBuilder();


    builder.append("union ").append(unionName).append(" = ");

    boolean needsJoinChar = false;
    for (String type : refs) {
      if (needsJoinChar) {
        builder.append(" | ");
      }
      builder.append(type);
      needsJoinChar = true;
    }
    builder.append("\n\n");
    return builder;
  }
}
