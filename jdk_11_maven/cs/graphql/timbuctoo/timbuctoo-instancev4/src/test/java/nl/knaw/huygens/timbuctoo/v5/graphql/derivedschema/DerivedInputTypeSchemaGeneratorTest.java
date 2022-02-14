package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema.PredicateBuilder.predicate;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DerivedInputTypeSchemaGeneratorTest {

  private static final String TYPE_URI = "http://example.org/type";
  private static final String TYPE = "Type";
  private static final String ROOT_TYPE = "rootType";
  private static final String READ_ONLY_PRED = "http://example.org/readOnly";
  private static final String READ_ONLY_TYPE = "http://example.org/readOnlyType";
  private DerivedInputTypeSchemaGenerator instanceNoProv;
  private DerivedInputTypeSchemaGenerator instanceProv;
  private GraphQlNameGenerator graphQlNameGenerator;
  private ReadOnlyChecker readOnlyChecker;
  private DerivedSchemaContainer derivedSchemaContainer;

  @Before
  public void setUp() throws Exception {
    graphQlNameGenerator = mock(GraphQlNameGenerator.class);
    when(graphQlNameGenerator.createObjectTypeName(ROOT_TYPE, TYPE_URI)).thenReturn(TYPE);
    derivedSchemaContainer = mock(DerivedSchemaContainer.class);
    when(derivedSchemaContainer.propertyInputType(anyList())).thenReturn("PropertyInput");
    readOnlyChecker = new ReadOnlyChecker() {
      @Override
      public boolean isReadonlyPredicate(String predicateIri) {
        return READ_ONLY_PRED.equals(predicateIri);
      }

      @Override
      public boolean isReadonlyType(String typeUri) {
        return READ_ONLY_TYPE.equals(typeUri);
      }
    };
    instanceNoProv = new DerivedInputTypeSchemaGenerator(
      TYPE_URI,
      ROOT_TYPE,
      graphQlNameGenerator,
      derivedSchemaContainer,
      readOnlyChecker,
      new CustomProvenance(Collections.emptyList())
    );
    instanceProv = new DerivedInputTypeSchemaGenerator(
      TYPE_URI,
      ROOT_TYPE,
      graphQlNameGenerator,
      derivedSchemaContainer,
      readOnlyChecker,
      CustomProvenance.getCustomProvenance(
        ImmutableMap.of("fields", Lists.newArrayList(
          ImmutableMap.of(
            "uri", "http://example.org/name",
            "isList", false,
            "valueType", STRING
          )
        ))
      )
    );
  }

  @Test
  public void createsAnEditInputType() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceNoProv.valueField(null, valueList, RdfConstants.STRING);


    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, containsString("input TypeEditInput {\n" +
      "  additions: TypeAdditionsInput\n" +
      "  deletions: TypeDeletionsInput\n" +
      "  replacements: TypeReplacementsInput\n" +
      "}\n\n"));
  }

  @Test
  public void createsAnEditInputTypeWithProvenance() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceProv.valueField(null, valueList, RdfConstants.STRING);


    String schema = instanceProv.getSchema().toString();

    assertThat(schema, containsString("input TypeEditInput {\n" +
      "  additions: TypeAdditionsInput\n" +
      "  deletions: TypeDeletionsInput\n" +
      "  replacements: TypeReplacementsInput\n" +
      "  provenance: rootTypeProvenanceInput\n" +
      "}\n\n"));
  }

  @Test
  public void createsAnCreateInputType() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceNoProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, containsString("input TypeCreateInput {\n" +
      "  creations: TypeCreationsInput\n" +
      "}\n\n"));
  }

  @Test
  public void createsAnCreateInputTypeWithProvenance() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceProv.getSchema().toString();

    assertThat(schema, containsString("input TypeCreateInput {\n" +
      "  creations: TypeCreationsInput\n" +
      "  provenance: rootTypeProvenanceInput\n" +
      "}\n\n"));
  }

  @Test
  public void createsADeleteInputTypeWithProvenance() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceProv.getSchema().toString();

    assertThat(schema, containsString("input TypeDeleteInput {\n" +
      "  provenance: rootTypeProvenanceInput\n" +
      "}\n\n"));
  }

  private void graphQlNameForPredicate(String predName, boolean asList, String graphQlName) {
    when(graphQlNameGenerator.createFieldName(predName, Direction.OUT, asList)).thenReturn(graphQlName);
  }

  @Test
  public void addsAdditionsDeletionsAndReplacementsInput() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .withValueType(RdfConstants.STRING)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .withValueType(RdfConstants.STRING)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceNoProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeAdditionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeDeletionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeReplacementsInput {\n" +
        "  short_singleValue: PropertyInput\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n")
    ));
  }

  @Test
  public void addsCreationsInput() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .withValueType(RdfConstants.STRING)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .withValueType(RdfConstants.STRING)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.valueField(null, valueNonList, RdfConstants.STRING);
    instanceNoProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeCreationsInput {\n" +
        "  short_singleValue: PropertyInput\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n")
    ));
  }

  @Test
  public void createAnEmptySchemaWhenNoPropertiesAreAdded() {
    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, isEmptyString());
  }

  @Test
  public void addsNoAdditionsOrDeletionsWhenNoListPredicatesAreAdded() {
    Predicate valueNonList = predicate().withName("http://example.com/valueNonList")
                                        .hasDirection(Direction.OUT)
                                        .build();
    graphQlNameForPredicate("http://example.com/valueNonList", false, "short_singleValue");
    instanceNoProv.valueField(null, valueNonList, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      not(containsString("input TypeAdditionsInput {\n" +
        "}\n\n")),
      not(containsString("input TypeDeletionsInput {\n" +
        "}\n\n")),
      containsString("input TypeReplacementsInput {\n" +
        "  short_singleValue: PropertyInput\n" +
        "}\n\n"),
      containsString("input TypeEditInput {\n" +
        "  replacements: TypeReplacementsInput\n" +
        "}\n\n")
    ));
  }

  @Test
  public void addsADeprecatedInputFieldForSingleForAListThatWasASingle() {
    Predicate valueList = predicate().withName("http://example.com/wasSingleList")
                                     .isList()
                                     .hasBeenSingular()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/wasSingleList", false, "short_wasSingle");
    graphQlNameForPredicate("http://example.com/wasSingleList", true, "short_wasSingleList");
    instanceNoProv.valueField(null, valueList, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeReplacementsInput {\n" +
        "  short_wasSingleList: [PropertyInput!]\n" +
        "  short_wasSingle: PropertyInput" +
        " @deprecated(reason: \"This property only returns the first value of the list. Use the *List version\")\n" +
        "}\n\n")
    ));
  }

  @Test
  public void addsADeprecatedInputFieldForAListThatIsNotUsed() {
    Predicate unusedPred = predicate().withName("http://example.com/unused")
                                      .notInUse()
                                      .hasBeenList()
                                      .hasDirection(Direction.OUT)
                                      .build();
    graphQlNameForPredicate("http://example.com/unused", true, "short_wasListList");
    instanceNoProv.valueField(null, unusedPred, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeReplacementsInput {\n" +
        "  short_wasListList: [PropertyInput!]" +
        " @deprecated(reason: \"There used to be entities with this property, but that is no longer the case.\")\n" +
        "}\n\n")
    ));
  }

  @Test
  public void addsADeprecatedInputFieldForASingleValueThatIsNotUsed() {
    Predicate unusedPred = predicate().withName("http://example.com/unused")
                                      .notInUse()
                                      .hasBeenSingular()
                                      .hasDirection(Direction.OUT)
                                      .build();
    graphQlNameForPredicate("http://example.com/unused", false, "short_unUsed");
    instanceNoProv.valueField(null, unusedPred, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema,containsString("input TypeReplacementsInput {\n" +
        "  short_unUsed: PropertyInput" +
        " @deprecated(reason: \"There used to be entities with this property, but that is no " +
        "longer the case.\")\n" +
        "}\n\n"));
  }

  @Test
  public void doesNotDeprecateWhenTheFieldIsExplicitAndUnused() {
    Predicate unusedPred = predicate().withName("http://example.com/unused")
                                      .notInUse()
                                      .explicit()
                                      .hasBeenSingular()
                                      .hasDirection(Direction.OUT)
                                      .build();

    graphQlNameForPredicate("http://example.com/unused", false, "short_unUsed");
    instanceNoProv.valueField(null, unusedPred, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema,containsString("input TypeReplacementsInput {\n" +
      "  short_unUsed: PropertyInput\n" +
      "}\n\n"));
  }

  @Test
  public void addObjectFieldAddsField() {
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.objectField(null, valueList, "http://example.org/person");

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeCreationsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeAdditionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeDeletionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeReplacementsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n")
    ));
  }

  @Test
  public void addUnionAddsField() {
    String type1 = "http://example.org/type1";
    String type2 = "http://example.org/type2";
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .withValueType(type1)
                                     .withValueType(type2)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");

    instanceNoProv.unionField(null, valueList, Sets.newHashSet(type1, type2));
    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, allOf(
      containsString("input TypeCreationsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeAdditionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeDeletionsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n"),
      containsString("input TypeReplacementsInput {\n" +
        "  short_multiValueList: [PropertyInput!]\n" +
        "}\n\n")
    ));
  }

  @Test
  public void returnsAnEmptyStringForReadOnlyTypes() {
    DerivedInputTypeSchemaGenerator instance = new DerivedInputTypeSchemaGenerator(
      READ_ONLY_TYPE,
      ROOT_TYPE,
      graphQlNameGenerator,
      derivedSchemaContainer,
      readOnlyChecker,
      new CustomProvenance(Collections.emptyList())
    );
    Predicate predicate = predicate().withName("http://example.com/value")
                                     .hasDirection(Direction.OUT)
                                     .withValueType(RdfConstants.STRING)
                                     .build();
    instance.valueField(null, predicate, RdfConstants.STRING);

    String schema = instance.getSchema().toString();
    assertThat(schema, isEmptyString());
  }

  @Test
  public void doesNotAddReadOnlyProperties() {
    Predicate predicate = predicate().withName(READ_ONLY_PRED)
                                     .hasDirection(Direction.OUT)
                                     .withValueType(RdfConstants.STRING)
                                     .build();
    instanceNoProv.valueField(null, predicate, RdfConstants.STRING);

    String schema = instanceNoProv.getSchema().toString();
    assertThat(schema, isEmptyString());
  }

  @Test
  public void addsCreateAndEditAndDeleteMethodsToType() {
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceNoProv.objectField(null, valueList, "http://example.org/person");

    String schema = instanceNoProv.getSchema().toString();

    assertThat(schema, containsString("}\n\ntype TypeMutations {\n" +
      "  create(uri: String! entity: TypeCreateInput!): Type @createMutation(dataSet: \"rootType\" typeUri: \"" +
      TYPE_URI + "\")\n" +
      "  edit(uri: String! entity: TypeEditInput!): Type @editMutation(dataSet: \"rootType\")\n" +
      "  delete(uri: String!): RemovedEntity! @deleteMutation(dataSet: \"rootType\")\n"));
  }

  @Test
  public void addsCreateAndEditAndDeleteMethodsToTypeWithProvenance() {
    Predicate valueList = predicate().withName("http://example.com/valueList")
                                     .isList()
                                     .hasDirection(Direction.OUT)
                                     .build();
    graphQlNameForPredicate("http://example.com/valueList", true, "short_multiValueList");
    instanceProv.objectField(null, valueList, "http://example.org/person");

    String schema = instanceProv.getSchema().toString();

    assertThat(schema, containsString("}\n\ntype TypeMutations {\n" +
      "  create(uri: String! entity: TypeCreateInput!): Type @createMutation(dataSet: \"rootType\" typeUri: \"" +
      TYPE_URI + "\")\n" +
      "  edit(uri: String! entity: TypeEditInput!): Type @editMutation(dataSet: \"rootType\")\n" +
      "  delete(uri: String! entity: TypeDeleteInput): RemovedEntity! @deleteMutation(dataSet: \"rootType\")\n"));
  }
}
