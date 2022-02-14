package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ProvenanceChangeLog;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ChangeMatcher.likeChange;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProvenanceChangeLogTest {
  private static final String TYPE_FIELD = "schema_Person";
  private static final String TYPE_URI = "http://schema.org/Person";
  private static final String SUBJECT = "http://example.org/subject";

  private static final String SUBJECT_NAME_1 = "http://schema.org/name1";
  private static final String SUBJECT_NAME_2 = "http://schema.org/name2";

  private static final String NAME_FIELD = "schema_name";
  private static final String NAME_URI = "http://schema.org/name";

  private static final String FIRST_NAME_FIELD = "schema_name_first";
  private static final String FIRST_NAME_URI = "http://schema.org/name_first";

  private static final String GRAPH_QL_STRING = "xsd_string";
  private DataSet dataSet;
  private DataSetMetaData dataSetMetaData;
  private TypeNameStore typeNameStore;

  @Before
  public void setUp() throws Exception {
    dataSet = mock(DataSet.class);
    dataSetMetaData = mock(DataSetMetaData.class);
    typeNameStore = mock(TypeNameStore.class);

    when(dataSet.getTypeNameStore()).thenReturn(typeNameStore);
    when(dataSet.getMetadata()).thenReturn(dataSetMetaData);
    when(dataSetMetaData.getBaseUri()).thenReturn("http://example.org/datasets/rootType/");

    when(typeNameStore.makeGraphQlnameForPredicate(TYPE_URI, Direction.OUT, false))
      .thenReturn(TYPE_FIELD);

    when(typeNameStore.makeUriForPredicate(NAME_FIELD))
      .thenReturn(Optional.of(tuple(NAME_URI, Direction.OUT)));
    when(typeNameStore.makeGraphQlnameForPredicate(NAME_URI, Direction.OUT, false))
      .thenReturn(NAME_FIELD);
    when(typeNameStore.makeGraphQlnameForPredicate(NAME_URI, Direction.OUT, true))
      .thenReturn(NAME_FIELD);

    when(typeNameStore.makeUriForPredicate(FIRST_NAME_FIELD))
      .thenReturn(Optional.of(tuple(FIRST_NAME_URI, Direction.OUT)));
    when(typeNameStore.makeGraphQlnameForPredicate(FIRST_NAME_URI, Direction.OUT, false))
      .thenReturn(FIRST_NAME_FIELD);
    when(typeNameStore.makeGraphQlnameForPredicate(FIRST_NAME_URI, Direction.OUT, true))
      .thenReturn(FIRST_NAME_FIELD);

    when(typeNameStore.makeUri(GRAPH_QL_STRING)).thenReturn(STRING);
  }

  @Test
  public void getProvenanceForField() throws Exception {
    CustomProvenance customProvenance = CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", false,
          "valueType", STRING
        )
      ))
    );
    when(dataSet.getCustomProvenance()).thenReturn(customProvenance);

    String value = "value";
    Map<Object, Object> provenance = Maps.newHashMap();
    provenance.put(NAME_FIELD, createPropertyInput(value));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("provenance", provenance);
    ProvenanceChangeLog instance = new ProvenanceChangeLog(entity);

    List<Change> provChanges = instance.getProvenance(dataSet, SUBJECT).collect(toList());

    assertThat(provChanges.size(), is(1));
    assertThat(provChanges.get(0), is(likeChange()
      .withSubject(SUBJECT)
      .withPredicate(NAME_URI)
      .withValues(new Value(value, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getProvenanceForListField() throws Exception {
    CustomProvenance customProvenance = CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", true,
          "valueType", STRING
        )
      ))
    );
    when(dataSet.getCustomProvenance()).thenReturn(customProvenance);

    String value1 = "value1";
    String value2 = "value2";
    Map<Object, Object> provenance = Maps.newHashMap();
    provenance.put(NAME_FIELD, newArrayList(createPropertyInput(value1), createPropertyInput(value2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("provenance", provenance);
    ProvenanceChangeLog instance = new ProvenanceChangeLog(entity);

    List<Change> provChanges = instance.getProvenance(dataSet, SUBJECT).collect(toList());

    assertThat(provChanges.size(), is(1));
    assertThat(provChanges.get(0), is(likeChange()
      .withSubject(SUBJECT)
      .withPredicate(NAME_URI)
      .withValues(new Value(value1, STRING), new Value(value2, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getProvenanceForObjectField() throws Exception {
    CustomProvenance customProvenance = CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", false,
          "object", ImmutableMap.of(
            "type", TYPE_URI,
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
    );
    when(dataSet.getCustomProvenance()).thenReturn(customProvenance);

    String value = "value";
    Map<Object, Object> name = Maps.newHashMap();
    name.put("uri", SUBJECT_NAME_1);
    name.put(FIRST_NAME_FIELD, createPropertyInput(value));
    Map<Object, Object> provenance = Maps.newHashMap();
    provenance.put(NAME_FIELD, name);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("provenance", provenance);
    ProvenanceChangeLog instance = new ProvenanceChangeLog(entity);

    List<Change> provChanges = instance.getProvenance(dataSet, SUBJECT).collect(toList());

    assertThat(provChanges.size(), is(3));
    assertThat(provChanges.get(0), is(likeChange()
      .withSubject(SUBJECT)
      .withPredicate(NAME_URI)
      .withValues(new Value(SUBJECT_NAME_1, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(1), is(likeChange()
      .withSubject(SUBJECT_NAME_1)
      .withPredicate(RDF_TYPE)
      .withValues(new Value(TYPE_URI, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(2), is(likeChange()
      .withSubject(SUBJECT_NAME_1)
      .withPredicate(FIRST_NAME_URI)
      .withValues(new Value(value, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getProvenanceForListObjectField() throws Exception {
    CustomProvenance customProvenance = CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", true,
          "object", ImmutableMap.of(
            "type", TYPE_URI,
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
    );
    when(dataSet.getCustomProvenance()).thenReturn(customProvenance);

    String value1 = "value1";
    Map<Object, Object> name1 = Maps.newHashMap();
    name1.put("uri", SUBJECT_NAME_1);
    name1.put(FIRST_NAME_FIELD, createPropertyInput(value1));
    Map<Object, Object> name2 = Maps.newHashMap();
    name2.put("uri", SUBJECT_NAME_2);

    String value2 = "value2";
    name2.put(FIRST_NAME_FIELD, createPropertyInput(value2));
    Map<Object, Object> provenance = Maps.newHashMap();
    provenance.put(NAME_FIELD, newArrayList(name1, name2));
    Map<Object, Object> entity = Maps.newHashMap();

    entity.put("provenance", provenance);
    ProvenanceChangeLog instance = new ProvenanceChangeLog(entity);

    List<Change> provChanges = instance.getProvenance(dataSet, SUBJECT).collect(toList());

    assertThat(provChanges.size(), is(6));
    assertThat(provChanges.get(0), is(likeChange()
      .withSubject(SUBJECT)
      .withPredicate(NAME_URI)
      .withValues(new Value(SUBJECT_NAME_1, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(1), is(likeChange()
      .withSubject(SUBJECT_NAME_1)
      .withPredicate(RDF_TYPE)
      .withValues(new Value(TYPE_URI, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(2), is(likeChange()
      .withSubject(SUBJECT_NAME_1)
      .withPredicate(FIRST_NAME_URI)
      .withValues(new Value(value1, STRING))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(3), is(likeChange()
      .withSubject(SUBJECT)
      .withPredicate(NAME_URI)
      .withValues(new Value(SUBJECT_NAME_2, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(4), is(likeChange()
      .withSubject(SUBJECT_NAME_2)
      .withPredicate(RDF_TYPE)
      .withValues(new Value(TYPE_URI, null))
      .oldValuesIsEmpty()
    ));
    assertThat(provChanges.get(5), is(likeChange()
      .withSubject(SUBJECT_NAME_2)
      .withPredicate(FIRST_NAME_URI)
      .withValues(new Value(value2, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getProvenanceForObjectFieldWihoutUri() throws Exception {
    CustomProvenance customProvenance = CustomProvenance.getCustomProvenance(
      ImmutableMap.of("fields", Lists.newArrayList(
        ImmutableMap.of(
          "uri", NAME_URI,
          "isList", false,
          "object", ImmutableMap.of(
            "type", TYPE_URI,
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
    );
    when(dataSet.getCustomProvenance()).thenReturn(customProvenance);

    String value = "value";
    Map<Object, Object> name = Maps.newHashMap();
    name.put(FIRST_NAME_FIELD, createPropertyInput(value));
    Map<Object, Object> provenance = Maps.newHashMap();
    provenance.put(NAME_FIELD, name);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("provenance", provenance);
    ProvenanceChangeLog instance = new ProvenanceChangeLog(entity);

    List<Change> provChanges = instance.getProvenance(dataSet, SUBJECT).collect(toList());

    assertThat(provChanges.get(1).getSubject(), startsWith("http://example.org/datasets/rootType/schema_Person/"));
    assertThat(provChanges.get(2).getSubject(), startsWith("http://example.org/datasets/rootType/schema_Person/"));
  }

  private Map<Object, Object> createPropertyInput(String value) {
    Map<Object, Object> propertyInput = Maps.newHashMap();
    propertyInput.put("type", GRAPH_QL_STRING);
    propertyInput.put("value", value);
    return propertyInput;
  }
}
