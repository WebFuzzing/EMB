package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

public class GraphQlNameGenerator {
  private final TypeNameStore typeNameStore;

  public GraphQlNameGenerator(TypeNameStore typeNameStore) {

    this.typeNameStore = typeNameStore;
  }

  public String graphQlUri(String uri) {
    // quotes and backslashes are not allowed in uri's anyway so this shouldn't happen
    // http://facebook.github.io/graphql/October2016/#sec-String-Value
    return uri.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  public String graphQlName(String uri) {
    return  typeNameStore.makeGraphQlname(uri);
  }

  public String createObjectTypeName(String rootType, String typeUri) {
    return rootType + "_" + typeNameStore.makeGraphQlname(typeUri);
  }

  public String createValueTypeName(String rootType, String typeUri) {
    //rootType prefix logic is also present in the ObjectTypeResolver of RdfWiringFactory
    return rootType + "_" + typeNameStore.makeGraphQlValuename(typeUri);
  }

  public String createFieldName(String uri, Direction direction, boolean asList) {
    return typeNameStore.makeGraphQlnameForPredicate(uri, direction, asList);
  }

  public String shorten(String typeUri) {
    return typeNameStore.shorten(typeUri);
  }

}
