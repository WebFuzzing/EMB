package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.PaginationArgumentsHelper;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DerivedSchemaContainer {
  private final Map<String, DerivedTypeSchemaGenerator> types;
  private final Map<String, DerivedObjectTypeSchemaGenerator> topLevelTypes;

  private final String rootType;
  private final GraphQlNameGenerator nameGenerator;
  private final PaginationArgumentsHelper argumentsHelper;
  private final ReadOnlyChecker readOnlyChecker;
  private final CustomProvenance customProvenance;

  DerivedSchemaContainer(String rootType, GraphQlNameGenerator graphQlNameGenerator,
                         PaginationArgumentsHelper argumentsHelper,
                         ReadOnlyChecker readOnlyChecker, CustomProvenance customProvenance) {
    this.rootType = rootType;
    this.nameGenerator = graphQlNameGenerator;
    this.argumentsHelper = argumentsHelper;

    types = new HashMap<>();
    topLevelTypes = new HashMap<>();
    this.readOnlyChecker = readOnlyChecker;
    this.customProvenance = customProvenance;
  }

  String propertyInputType(List<String> refs) {
    String typeName = nameGenerator.createObjectTypeName(rootType, refs.stream().sorted(String::compareTo)
                                                                       .reduce("", String::concat));

    String inputTypeName = typeName + "PropertyInput";

    if (!types.keySet().contains(inputTypeName)) {
      String enumTypeName = typeName + "PropertyInputEnum";
      types.put(inputTypeName, new DerivedTypeSchemaGenerator() {
        @Override
        public StringBuilder getSchema() {
          StringBuilder schema = new StringBuilder("input ").append(inputTypeName).append("{\n")
                                     .append("  type: ").append(enumTypeName).append("!\n")
                                     .append("  value: ").append(" String!\n")
                                     .append("}\n\n");
          schema.append("enum ").append(enumTypeName).append("{\n");
          refs.stream().map(nameGenerator::graphQlName)
                     .forEach(ref -> schema.append("  ").append(ref).append("\n"));
          schema.append("}\n\n");
          return schema;
        }
      });
    }

    return inputTypeName;
  }

  String listType(String fieldName, String typeName) {
    final String listTypeName = argumentsHelper.makeListName(typeName);
    if (!types.containsKey(listTypeName)) {
      DerivedTypeSchemaGenerator listSchema = new DerivedListTypeSchemaGenerator(typeName,
        DerivedSchemaContainer.this.argumentsHelper);
      types.put(listTypeName, listSchema);

    }
    return argumentsHelper.makeListField(fieldName, typeName);
  }

  String collectionType(String fieldName, String typeName) {
    final String listTypeName = argumentsHelper.makeCollectionListName(typeName);
    if (!types.containsKey(listTypeName)) {
      DerivedTypeSchemaGenerator derivedCollectionTypeSchemaGenerator =
        new DerivedCollectionTypeSchemaGenerator(typeName, DerivedSchemaContainer.this.argumentsHelper);

      types.put(listTypeName, derivedCollectionTypeSchemaGenerator);
    }
    return argumentsHelper.makeCollectionListField(fieldName + "List", typeName);
  }


  String unionType(Set<String> refs) {
    String unionName = "Union_";
    for (String type : refs) {
      unionName += type + "__";
    }
    if (!types.containsKey(unionName)) {
      DerivedTypeSchemaGenerator derivedUnionTypeSchemaGenerator = new DerivedUnionTypeSchemaGenerator(unionName, refs);
      types.put(unionName, derivedUnionTypeSchemaGenerator);
    }
    return unionName;
  }

  String valueType(String typeUri) {
    final String name = nameGenerator.createValueTypeName(rootType, typeUri);
    if (!types.containsKey(name)) {

      DerivedTypeSchemaGenerator derivedValueTypeSchemaGenerator = new DerivedValueTypeSchemaGenerator(name);


      types.put(name, derivedValueTypeSchemaGenerator);


    }
    return name;
  }


  DerivedObjectTypeSchemaGenerator addObjectType(String typeUri) {
    if (!topLevelTypes.containsKey(typeUri)) {
      DerivedObjectTypeSchemaGenerator value =
        new DerivedCompositeObjectTypeSchemaGenerator(typeUri, rootType, nameGenerator, this, readOnlyChecker,
          customProvenance);
      topLevelTypes.put(typeUri, value);
      return value;
    }

    return topLevelTypes.get(typeUri);
  }

  public String getSchema() {
    StringBuilder total = new StringBuilder();
    addRootType(total);
    addRootTypeMutations(total);

    for (DerivedTypeSchemaGenerator derivedObjectTypeSchemaGenerator : types.values()) {
      total.append(derivedObjectTypeSchemaGenerator.getSchema());
    }

    for (DerivedTypeSchemaGenerator derivedObjectTypeSchemaGenerator : topLevelTypes.values()) {
      total.append(derivedObjectTypeSchemaGenerator.getSchema());
    }

    return total.toString();
  }

  public boolean hasMutationTypes() {
    StringBuilder mutationsSchema = new StringBuilder();
    topLevelTypes.values().forEach(schemaGenerator -> schemaGenerator.addMutationToSchema(mutationsSchema));

    return mutationsSchema.length() > 0;
  }

  private void addRootTypeMutations(StringBuilder total) {
    if (topLevelTypes.isEmpty()) {
      return;
    }
    StringBuilder mutationsSchema = new StringBuilder();

    topLevelTypes.values().forEach(schemaGenerator -> schemaGenerator.addMutationToSchema(mutationsSchema));

    if (mutationsSchema.length() <= 0) {
      return;
    }

    total.append("type ").append(rootType).append("Mutations").append("{\n");
    total.append("  setCustomProvenance(customProvenance: CustomProvenanceInput!): Message!")
         .append(" @setCustomProvenanceMutation(dataSet: \"").append(rootType).append("\")\n");
    total.append("  resetIndex: Message! @resetIndex(dataSet: \"").append(rootType).append("\")\n");
    total.append(mutationsSchema);
    total.append("}\n\n");

    if (!customProvenance.getFields().isEmpty()) {
      addProvenanceInputType(total, customProvenance);
    }
  }

  private void addRootType(StringBuilder total) {
    total.append("type ").append(rootType).append("{\n");
    total.append("  metadata: DataSetMetadata!\n");
    total.append("  subject(uri: String!): Entity @lookupUri\n");

    for (DerivedObjectTypeSchemaGenerator schemaGenerator : topLevelTypes.values()) {
      schemaGenerator.addQueryToSchema(total);
    }
    total.append("}\n\n");
  }

  private void addProvenanceInputType(StringBuilder schema, CustomProvenance customProvenance) {
    boolean isCustomProvObjectFieldInput =
      (customProvenance instanceof CustomProvenance.CustomProvenanceObjectFieldInput);

    if (isCustomProvObjectFieldInput) {
      CustomProvenance.CustomProvenanceObjectFieldInput objectFieldInput =
        (CustomProvenance.CustomProvenanceObjectFieldInput) customProvenance;
      String objectTypeName = nameGenerator.createValueTypeName(rootType, objectFieldInput.getType());
      schema.append("input ").append(objectTypeName).append("{\n");
    } else {
      schema.append("input ").append(rootType).append("ProvenanceInput").append("{\n");
    }

    if (isCustomProvObjectFieldInput) {
      schema.append("  uri: String\n");
    }

    for (CustomProvenance.CustomProvenanceValueFieldInput field : customProvenance.getFields()) {
      addProvenanceField(schema, field);
    }

    schema.append("}\n\n");

    for (CustomProvenance.CustomProvenanceValueFieldInput field : customProvenance.getFields()) {
      if (field.getObject() != null) {
        addProvenanceInputType(schema, field.getObject());
      }
    }
  }

  private void addProvenanceField(StringBuilder schema, CustomProvenance.CustomProvenanceValueFieldInput field) {
    String type;
    if (field.getValueType() != null) {
      type = propertyInputType(Collections.singletonList(field.getValueType()));
    } else {
      type = nameGenerator.createValueTypeName(rootType, field.getObject().getType());
    }
    type = field.isList() ? "[" + type + "!]!" : type + "!";

    String fieldName = nameGenerator.createFieldName(field.getUri(), Direction.OUT, field.isList());
    schema.append("  ").append(fieldName).append(": ").append(type).append("\n");
  }

  String graphQlUri(String uri) {
    // quotes and backslashes are not allowed in uri's anyway so this shouldn't happen
    // http://facebook.github.io/graphql/October2016/#sec-String-Value
    return uri.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
