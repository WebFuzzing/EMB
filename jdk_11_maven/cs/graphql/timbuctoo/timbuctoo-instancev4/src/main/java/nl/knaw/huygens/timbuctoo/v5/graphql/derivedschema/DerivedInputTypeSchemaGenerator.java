package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;

import java.util.List;
import java.util.Set;

class DerivedInputTypeSchemaGenerator {


  private final String typeUri;
  private final String rootType;
  private final GraphQlNameGenerator graphQlNameGenerator;
  private final DerivedSchemaContainer derivedSchemaContainer;
  private final ReadOnlyChecker readOnlyChecker;
  private final List<GraphQlPredicate> creations;
  private final List<GraphQlPredicate> replacements;
  private final boolean isReadOnly;
  private final boolean hasCustomProvenance;
  private List<GraphQlPredicate> additions;
  private List<GraphQlPredicate> deletions;

  public DerivedInputTypeSchemaGenerator(String typeUri, String rootType, GraphQlNameGenerator graphQlNameGenerator,
                                         DerivedSchemaContainer derivedSchemaContainer,
                                         ReadOnlyChecker readOnlyChecker, CustomProvenance customProvenance) {
    this.typeUri = typeUri;
    this.rootType = rootType;
    this.graphQlNameGenerator = graphQlNameGenerator;
    this.derivedSchemaContainer = derivedSchemaContainer;
    this.readOnlyChecker = readOnlyChecker;
    creations = Lists.newArrayList();
    replacements = Lists.newArrayList();
    additions = Lists.newArrayList();
    deletions = Lists.newArrayList();
    isReadOnly = readOnlyChecker.isReadonlyType(typeUri);
    hasCustomProvenance = !customProvenance.getFields().isEmpty();
  }

  public void objectField(String description, Predicate predicate, String typeUri) {
    addPredicate(predicate);
  }

  public void unionField(String description, Predicate predicate, Set<String> typeUris) {
    addPredicate(predicate);
  }

  public void valueField(String description, Predicate predicate, String typeUri) {
    addPredicate(predicate);
  }

  private void addPredicate(Predicate predicate) {
    if (!isReadOnly && !readOnlyChecker.isReadonlyPredicate(predicate.getName())) {
      creations.add(new GraphQlPredicate(predicate));
      replacements.add(new GraphQlPredicate(predicate));
      if (predicate.isList()) {
        additions.add(new GraphQlPredicate(predicate));
        deletions.add(new GraphQlPredicate(predicate));
      }
    }
  }

  public StringBuilder getSchema() {
    StringBuilder schema = new StringBuilder();
    if (creations.isEmpty() && additions.isEmpty() && deletions.isEmpty() && replacements.isEmpty()) {
      return schema;
    }
    String name = graphQlNameGenerator.createObjectTypeName(rootType, typeUri);

    if (!creations.isEmpty()) {
      schema.append("input ").append(name).append("CreateInput").append(" {\n");
      schema.append("  creations: ").append(name).append("CreationsInput\n");
      if (hasCustomProvenance) {
        schema.append("  provenance: ").append(rootType).append("ProvenanceInput\n");
      }
      schema.append("}\n\n");
    }

    if (!additions.isEmpty() || !deletions.isEmpty() || !replacements.isEmpty()) {
      schema.append("input ").append(name).append("EditInput").append(" {\n");
      if (!additions.isEmpty()) {
        schema.append("  additions: ").append(name).append("AdditionsInput\n");
      }
      if (!deletions.isEmpty()) {
        schema.append("  deletions: ").append(name).append("DeletionsInput\n");
      }
      schema.append("  replacements: ").append(name).append("ReplacementsInput\n");
      if (hasCustomProvenance) {
        schema.append("  provenance: ").append(rootType).append("ProvenanceInput\n");
      }
      schema.append("}\n\n");
    }

    if (!deletions.isEmpty() && hasCustomProvenance) {
      schema.append("input ").append(name).append("DeleteInput").append(" {\n");
      schema.append("  provenance: ").append(rootType).append("ProvenanceInput\n");
      schema.append("}\n\n");
    }

    // Add the inputs
    if (!creations.isEmpty()) {
      inputFields(schema, name, "CreationsInput", creations);
    }

    if (!replacements.isEmpty()) {
      inputFields(schema, name, "ReplacementsInput", replacements);
    }

    if (!additions.isEmpty()) {
      inputFields(schema, name, "AdditionsInput", additions);
    }

    if (!deletions.isEmpty()) {
      inputFields(schema, name, "DeletionsInput", deletions);
    }

    schema.append("type ").append(name).append("Mutations").append(" {\n");

    if (!creations.isEmpty()) {
      schema.append("  create(").append("uri: String! ").append("entity: ").append(name).append("CreateInput!): ")
            .append(name).append(" @createMutation(dataSet: \"").append(rootType).append("\"")
            .append(" typeUri: \"").append(typeUri).append("\")").append("\n");
    }

    if (!additions.isEmpty() || !deletions.isEmpty() || !replacements.isEmpty()) {
      schema.append("  edit(").append("uri: String! ").append("entity: ").append(name).append("EditInput!): ")
            .append(name).append(" @editMutation(dataSet: \"").append(rootType).append("\")").append("\n");
    }

    if (!deletions.isEmpty() && !hasCustomProvenance) {
      schema.append("  delete(").append("uri: String!): RemovedEntity! ")
            .append("@deleteMutation(dataSet: \"").append(rootType).append("\")").append("\n");
    } else if (!deletions.isEmpty()) {
      schema.append("  delete(").append("uri: String! ").append("entity: ").append(name).append("DeleteInput): ")
            .append("RemovedEntity! @deleteMutation(dataSet: \"").append(rootType).append("\")").append("\n");
    }

    schema.append("  persistEntity(").append("entityUri: String!): ").append("Message")
          .append(" @persistEntityMutation(dataSet: \"").append(rootType).append("\")").append("}\n");

    return schema;
  }

  public void addMutationToSchema(StringBuilder schema) {
    if (!creations.isEmpty() || !replacements.isEmpty()) {
      String typename = graphQlNameGenerator.createObjectTypeName(rootType, typeUri);
      String name = typename.substring(rootType.length() + 1);
      schema.append("  ").append(name).append(": ").append(typename).append("Mutations").append(" @passThrough\n");
    }
  }

  private void inputFields(StringBuilder schema, String name, String actionName, List<GraphQlPredicate> predicates) {
    schema.append("input ").append(name).append(actionName).append(" {\n");
    for (GraphQlPredicate predicate : predicates) {
      predicate.addToSchema(schema, actionName.startsWith("Replacements"));
    }
    schema.append("}\n\n");
  }

  private class GraphQlPredicate {
    private final Predicate predicate;
    private String typeName;

    private GraphQlPredicate(Predicate predicate) {
      this.predicate = predicate;
      List<String> allTypes = Lists.newArrayList();
      allTypes.addAll(predicate.getUsedValueTypes());
      allTypes.addAll(predicate.getUsedReferenceTypes());
      typeName = derivedSchemaContainer.propertyInputType(allTypes);
    }

    public void addToSchema(StringBuilder schema, boolean isReplacements) {
      boolean asList = predicate.isList() || predicate.hasBeenList();
      schema.append("  ").append(predName(asList)).append(": ").append(getInputType(asList))
            .append(getDeprecation(!predicate.inUse(), asList, predicate.isExplicit())).append("\n");

      if (isReplacements && predicate.isList() && predicate.isHasBeenSingular()) {
        // add field for deprecated single value field
        schema.append("  ").append(predName(false)).append(": ").append(getInputType(false)).append(
          getDeprecation(true, false, false)).append("\n");
      }
    }

    private String getDeprecation(boolean isDeprecated, boolean asList, boolean explicit) {
      if (isDeprecated && !explicit) {
        if (asList) {
          return " @deprecated(reason: \"There used to be entities with this property, but that is no longer the case" +
            ".\")";
        }

        if (predicate.isList()) {
          return " @deprecated(reason: \"This property only returns the first value of the list. Use the *List " +
            "version\")";
        } else {
          return " @deprecated(reason: \"There used to be entities with this property, but that is no " +
            "longer the case.\")";
        }
      }
      return "";
    }

    private String predName(boolean asList) {
      return graphQlNameGenerator.createFieldName(predicate.getName(), predicate.getDirection(), asList);
    }

    private String getInputType(boolean asList) {
      return asList ? "[" + typeName + "!]" : typeName;
    }
  }

}
