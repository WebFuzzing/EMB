package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;

import java.util.Set;

class DerivedQueryObjectTypeSchemaGenerator {
  private static final String ENTITY_INTERFACE_NAME = "Entity";

  private final String typeUri;
  private StringBuilder builder;
  private StringBuilder predicates;
  private GraphQlNameGenerator graphQlNameGenerator;
  private String rootType;
  private final DerivedSchemaContainer derivedSchemaContainer;

  public DerivedQueryObjectTypeSchemaGenerator(String typeUri,
                                               String rootType, GraphQlNameGenerator graphQlNameGenerator,
                                               DerivedSchemaContainer derivedSchemaContainer) {
    this.typeUri = typeUri;
    this.graphQlNameGenerator = graphQlNameGenerator;
    this.rootType = rootType;
    this.derivedSchemaContainer = derivedSchemaContainer;
    this.builder = new StringBuilder();
    this.predicates = new StringBuilder();
  }

  public void objectField(String description, Predicate predicate, String typeUri) {
    makeFieldAndDeprecations(description, predicate, typeUri, false, true);
  }

  public void unionField(String description, Predicate predicate, Set<String> typeUris) {
    String unionType = derivedSchemaContainer.unionType(typeUris);
    makeFieldAndDeprecations(description, predicate, unionType, true, true);
  }

  public void valueField(String description, Predicate predicate, String typeUri) {
    String type = derivedSchemaContainer.valueType(typeUri);
    makeFieldAndDeprecations(description, predicate, type, true, false);
  }

  private void makeFieldAndDeprecations(String description, Predicate predicate, String targetType, boolean isValue,
                                        boolean isObject) {
    if (predicate.inUse() || predicate.isExplicit()) {
      //once a list, always a list
      if (predicate.isList() || predicate.hasBeenList()) {
        makeField(description, predicate, targetType, isValue, isObject, true);
        if (predicate.isHasBeenSingular()) {
          makeField(description, predicate, targetType, isValue, isObject, false);
          predicates.append(
            " @deprecated(reason: \"This property only returns the first value of the list. Use the *List version to " +
              "retrieve all value\")\n");
        }
      } else {
        //never been a list
        makeField(description, predicate, targetType, isValue, isObject, false);
      }
    } else {
      if (predicate.hasBeenList()) {
        makeField(description, predicate, targetType, isValue, isObject, true);
        predicates.append(" @deprecated(reason: \"There used to be entities with this property, but that is no " +
          "longer the case.\")\n");
      }
      if (predicate.isHasBeenSingular()) {
        makeField(description, predicate, targetType, isValue, isObject, false);
        predicates.append(" @deprecated(reason: \"There used to be entities with this property, but that is no " +
          "longer the case.\")\n");
      }
    }

    predicates.append("\n");
  }


  private void makeField(String description, Predicate predicate, String targetType, boolean isValue,
                         boolean isObject, boolean asList) {
    String fieldName = graphQlNameGenerator.createFieldName(predicate.getName(), predicate.getDirection(), asList);
    if (description != null) {
      predicates.append("  #").append(description).append("\n");
    }

    predicates.append("  ");
    if (asList) {
      predicates.append(derivedSchemaContainer.listType(fieldName, targetType));
    } else {
      predicates.append(fieldName).append(": ").append(targetType);
    }
    final String safeName = predicate.getName().replace("\"", "");
    predicates.append(" ")
           .append("@rdf(predicate: \"")
           .append(safeName)
           .append("\", direction: \"")
           .append(predicate.getDirection())
           .append("\", ")
           .append("isValue: ")
           .append(isValue)
           .append(", isObject: ")
           .append(isObject)
           .append(", isList: ")
           .append(asList)
           .append(")");
  }


  public StringBuilder getSchema() {
    String name = graphQlNameGenerator.createObjectTypeName(rootType, typeUri);
    builder.append("#")
           .append("Subjects that are a [")
           .append(graphQlNameGenerator.shorten(typeUri))
           .append("](")
           .append(typeUri)
           .append(")")
           .append("\n")
           .append("type ").append(name).append(" implements ").append(ENTITY_INTERFACE_NAME)
           .append(" @rdfType(uri: \"")
           //quotes and backslashes are not allowed in uri's anyway so this shouldn't happen
           .append(graphQlNameGenerator.graphQlUri(typeUri))
           .append("\") {\n")
           .append("  uri: String! @uri\n")
           .append("  title: Value @entityTitle\n")
           .append("  description: Value @entityDescription\n")
           .append("  image: Value @entityImage\n")
           .append("  getAllOfPredicate(uri: String!, outgoing: Boolean!, cursor: ID, count: Int): EntityOrValueList " +
             "@getAllOfPredicate\n")
           .append("  inOtherDataSets(dataSetIds: [String!]): [DataSetLink!]! @otherDataSets\n");
    builder.append(predicates);
    builder.append("}\n\n");
    return builder;
  }

  public void addQueryToSchema(StringBuilder schema) {
    String typename = graphQlNameGenerator.createObjectTypeName(rootType, typeUri );
    String name = typename.substring(rootType.length() + 1);
    schema.append("  ").append(name).append("(uri: String!)").append(": ").append(typename).append(" @lookupUri\n");
    schema.append("  ")
         .append(derivedSchemaContainer.collectionType(name, typename))
         .append(" @fromCollection(uri: \"")
         .append(derivedSchemaContainer.graphQlUri(typeUri))
         .append("\")\n");
  }
}
