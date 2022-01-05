package nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema;

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.PaginationArgumentsHelper;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static nl.knaw.huygens.timbuctoo.v5.graphql.derivedschema.PredicateBuilder.predicate;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DerivedSchemaContainerTest {
  private static final String ROOT_TYPE = "rootType";
  private static final String OBJECT_TYPE = "rootType_schema_Book";
  private static final String OBJECT_TYPE_URI = "http://schema.org/Book";

  private static final String PERSON_TYPE = "rootType_schema_Person";
  private static final String PERSON_TYPE_VALUE = "rootType_value_schema_Person";
  private static final String PERSON_TYPE_URI = "http://schema.org/Person";

  private static final String VALUE_TYPE = "rootType_schema_title";
  private static final String VALUE_TYPE_URI = "http://schema.org/title";

  private static final String NAME_FIELD = "schema_name";
  private static final String NAME_URI = "http://schema.org/name";

  private static final String FIRST_NAME_FIELD = "schema_name_first";
  private static final String FIRST_NAME_URI = "http://schema.org/name_first";

  private static final String GRAPH_QL_STRING = "xsd_string";

  private GraphQlNameGenerator nameGenerator;
  private PaginationArgumentsHelper argumentsHelper;
  private ReadOnlyChecker readOnlyChecker;

  @Before
  public void setUp() throws Exception {
    nameGenerator = mock(GraphQlNameGenerator.class);

    when(nameGenerator.graphQlName(STRING)).thenReturn(GRAPH_QL_STRING);

    when(nameGenerator.createObjectTypeName(ROOT_TYPE, STRING)).thenReturn(GRAPH_QL_STRING);
    when(nameGenerator.createObjectTypeName(ROOT_TYPE, OBJECT_TYPE_URI)).thenReturn(OBJECT_TYPE);
    when(nameGenerator.createObjectTypeName(ROOT_TYPE, PERSON_TYPE_URI)).thenReturn(PERSON_TYPE);

    when(nameGenerator.createValueTypeName(ROOT_TYPE, VALUE_TYPE_URI)).thenReturn(VALUE_TYPE);
    when(nameGenerator.createValueTypeName(ROOT_TYPE, PERSON_TYPE_URI)).thenReturn(PERSON_TYPE_VALUE);

    when(nameGenerator.createFieldName(eq(NAME_URI), eq(Direction.OUT), anyBoolean())).thenReturn(NAME_FIELD);
    when(nameGenerator.createFieldName(eq(FIRST_NAME_URI), eq(Direction.OUT), anyBoolean()))
      .thenReturn(FIRST_NAME_FIELD);

    argumentsHelper = mock(PaginationArgumentsHelper.class);
    readOnlyChecker = mock(ReadOnlyChecker.class);
  }

  @Test
  public void addsCustomProvenanceCreationMethod() {
    DerivedSchemaContainer instance = createWithProvenance(new CustomProvenance(newArrayList()));

    String schema = instance.getSchema();

    assertThat(schema, containsString(
      "type rootTypeMutations{\n" +
        "  setCustomProvenance(customProvenance: CustomProvenanceInput!): Message!" +
        " @setCustomProvenanceMutation(dataSet: \"rootType\")\n"
    ));
  }

  @Test
  public void addProvenanceSchemaForField() throws Exception {
    DerivedSchemaContainer instance = createWithProvenance(CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", false,
          "valueType", STRING
        )
      ))
    ));

    String schema = instance.getSchema();

    assertThat(schema, containsString(
      "input rootTypeProvenanceInput{\n" +
        "  schema_name: xsd_stringPropertyInput!"
    ));
  }

  @Test
  public void addProvenanceSchemaForListField() throws Exception {
    DerivedSchemaContainer instance = createWithProvenance(CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", true,
          "valueType", STRING
        )
      ))
    ));

    String schema = instance.getSchema();

    assertThat(schema, containsString(
      "input rootTypeProvenanceInput{\n" +
        "  schema_name: [xsd_stringPropertyInput!]!"
    ));
  }

  @Test
  public void addProvenanceSchemaForObjectField() throws Exception {
    DerivedSchemaContainer instance = createWithProvenance(CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", false,
          "object", ImmutableMap.of(
            "type", PERSON_TYPE_URI,
            "fields", Lists.newArrayList(
              ImmutableMap.of(
                "uri", FIRST_NAME_URI,
                "isList", false,
                "valueType", STRING
              )
            )
          )
        )
      ))
    ));

    String schema = instance.getSchema();

    assertThat(schema, containsString(
      "input rootTypeProvenanceInput{\n" +
        "  schema_name: rootType_value_schema_Person!\n" +
        "}\n\n" +
        "input rootType_value_schema_Person{\n" +
        "  uri: String\n" +
        "  schema_name_first: xsd_stringPropertyInput!\n" +
        "}"
    ));
  }

  @Test
  public void addProvenanceSchemaForListObjectField() throws Exception {
    DerivedSchemaContainer instance = createWithProvenance(CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", true,
          "object", ImmutableMap.of(
            "type", PERSON_TYPE_URI,
            "fields", Lists.newArrayList(
              ImmutableMap.of(
                "uri", FIRST_NAME_URI,
                "isList", false,
                "valueType", STRING
              )
            )
          )
        )
      ))
    ));

    String schema = instance.getSchema();

    assertThat(schema, containsString(
      "input rootTypeProvenanceInput{\n" +
        "  schema_name: [rootType_value_schema_Person!]!\n" +
        "}\n\n" +
        "input rootType_value_schema_Person{\n" +
        "  uri: String\n" +
        "  schema_name_first: xsd_stringPropertyInput!\n" +
        "}"
    ));
  }

  private DerivedSchemaContainer createWithProvenance(CustomProvenance customProvenance) {
    DerivedSchemaContainer instance = new DerivedSchemaContainer(
      ROOT_TYPE,
      nameGenerator,
      argumentsHelper,
      readOnlyChecker,
      customProvenance
    );

    Predicate predicate = predicate().withName(VALUE_TYPE_URI)
                                     .hasDirection(Direction.OUT)
                                     .build();
    DerivedObjectTypeSchemaGenerator typeSchemaGenerator = instance.addObjectType(OBJECT_TYPE_URI);
    typeSchemaGenerator.valueField(null, predicate, STRING);

    return instance;
  }
}
