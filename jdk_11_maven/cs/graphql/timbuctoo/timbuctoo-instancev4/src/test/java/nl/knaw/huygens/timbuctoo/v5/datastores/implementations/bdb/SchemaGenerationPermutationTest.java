package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.google.common.collect.Lists;
import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.ListPartitioner;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.IsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.orderedPermutations;
import static java.util.Arrays.asList;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType.ASSERTED;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType.RETRACTED;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.IN;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;
import static nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.PredicateMatcher.predicate;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.UNKNOWN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@Ignore // disable test because our docker hub build fails with this test on
@RunWith(Parameterized.class)
public class SchemaGenerationPermutationTest {
  private static final String USER = "user";
  private static final String DATA_SET = "dataSet";
  private static final StringStringIsCleanHandler STRING_IS_CLEAN_HANDLER = new StringStringIsCleanHandler();
  private static final TupleBinding<String> STRING_BINDING = TupleBinding.getPrimitiveBinding(String.class);

  private static final String SUBJECT_A = "http://example.org/foo";
  private static final String SUBJECT_B = "http://example.org/bar";
  private static final String SUBJECT_C = "http://example.org/baz";

  private static final String TYPE_1 = "http://example.org/type";
  private static final String TYPE_2 = "http://example.org/footype";
  private static final String TYPE_3 = "http://example.org/barType";

  private static final String PROP_I = "http://example.org/pred1";
  private static final String PROP_II = "http://example.org/pred2";

  private static final String GRAPH = "http://example.org";

  private final List<List<CursorQuad>> input;
  private final Matcher<Map<String, Type>> result;
  
  public SchemaGenerationPermutationTest(List<List<CursorQuad>> input, Matcher<Map<String, Type>> result) {
    this.input = input;
    this.result = result;
  }

  // Run the cases one by one
  // Some test generate a large amount of permutations
  @Parameters(name = "{index}: schema for {0}")
  public static Collection<Object[]> getData() {
    List<Object[]> testCases = Lists.newArrayList();
    // predicateOfASubjectIsAddedToEachType
    testCases.addAll(createPermutationsOfTestCase(
      allOf(
        hasEntry(is(TYPE_1), hasProperty("name", is(TYPE_1))),
        hasEntry(is(TYPE_1), hasProperty("name", is(TYPE_1))),
        hasEntry(is(TYPE_3), hasProperty("name", is(TYPE_3)))
      ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_1, null, null, ""),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_B, RDF_TYPE, OUT, ASSERTED, TYPE_3, null, null, "")
    ));
    // predicateOfASubjectIsAddedToEachType
    testCases.addAll(createPermutationsOfTestCase(
      allOf(
        hasEntry(is(TYPE_1), hasProperty("predicates", allOf(
          hasItem(predicate().withName(PROP_I)),
          hasItem(predicate().withName(PROP_II))
        ))),
        hasEntry(is(TYPE_2), hasProperty("predicates", allOf(
          hasItem(predicate().withName(PROP_I)),
          hasItem(predicate().withName(PROP_II))
        )))
      ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_1, null, null, ""),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_II, OUT, ASSERTED, SUBJECT_B, null, null, "")
    ));
    // eachPredicateThatLinksToAnotherSubjectWillBeAddedToTheOtherSubjectAsAnIncoming
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(OUT).withReferenceType(TYPE_3))
      ))),
      hasEntry(is(TYPE_3), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(IN).withReferenceType(TYPE_2))
      )))
      ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_B, RDF_TYPE, OUT, ASSERTED, TYPE_3, null, null, "")
    ));
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates",allOf(
        hasItem(predicate().withName(PROP_I).withDirection(OUT).withReferenceType(TYPE_3))
      ))),
      hasEntry(is(TYPE_3), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(IN).withReferenceType(TYPE_2))
      )))
    ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_B, RDF_TYPE, OUT, ASSERTED, TYPE_3, null, null, "")
    ));
    // ifTheReferencedSubjectHasNoTypeThePredicateWillBeAddedToTimUnknown
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(OUT).withReferenceType(UNKNOWN))
      ))),
      hasEntry(is(UNKNOWN), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(IN).withReferenceType(TYPE_2))
      )))
      ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, "")
    ));
    // theValueTypeIsAddedToThePredicate
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates",
        hasItem(predicate().withName(PROP_I).withValueType("http://example.org/valuetype"))
      ))),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, "value", "http://example.org/valuetype", null, "")
    ));
    // thePredicateWillBecomeAListWhenASubjectHasMultipleInstances
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates",
        hasItem(predicate().withName(PROP_I).withIsList(true).withValueTypeCount(2))
      ))),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, "value", "http://example.org/valuetype", null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, "value2", "http://example.org/valuetype", null, "")
    ));
    // predicateIsAlsoAListWhenItHasDifferentTypes
    testCases.addAll(createPermutationsOfTestCase(
      allOf(hasEntry(is(TYPE_2), hasProperty("predicates", hasItem(
        predicate().withName(PROP_I).withReferenceType(TYPE_3)
                   .withValueType("http://example.org/valuetype").withIsList(true)
      )))),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, "value", "http://example.org/valuetype", null, ""),
      CursorQuad.create(SUBJECT_B, RDF_TYPE, OUT, ASSERTED, TYPE_3, null, null, "")
    ));
    // inversePredicatesAreNotAlwaysLists
    testCases.addAll(createPermutationsOfTestCase(allOf(
      hasEntry(is(TYPE_2), hasProperty("predicates",
        hasItem(predicate().withName(PROP_I).withDirection(OUT).withIsList(true))
      )),
      hasEntry(is(UNKNOWN), hasProperty("predicates",
        hasItem(predicate().withName(PROP_I).withDirection(IN).withIsList(false))
      ))),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_2, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_C, null, null, "")
    ));
    // aDoubleAssertionOfATripleDoesNotIncreaseTheReferenceCounts
    testCases.addAll(createPermutationsOfTestCase(
      allOf(hasEntry(is(UNKNOWN), hasProperty("predicates", allOf(
        hasItem(predicate().withName(PROP_I).withDirection(IN).withReferenceTypeCount(1)),
        hasItem(predicate().withName(PROP_I).withDirection(OUT).withReferenceTypeCount(1))
      )))),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, "")
    ));
    // retractingATripleLeavesTheTypesInTheSchema
    // TODO fix tests? Schema ignores predicates that are asserted and retracted in one session
    testCases.addAll(createPartitionsOfTestCase( // use partitions, because the case contains a retraction
      allOf(
        hasEntry(is(TYPE_1), hasProperty("name", is(TYPE_1)))
      ),
      CursorQuad.create(SUBJECT_A, RDF_TYPE, OUT, ASSERTED, TYPE_1, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, ASSERTED, SUBJECT_B, null, null, ""),
      CursorQuad.create(SUBJECT_A, PROP_I, OUT, RETRACTED, SUBJECT_B, null, null, "")
    ));
    return testCases;
  }

  private static List<Object[]> createPartitionsOfTestCase(Matcher<Map<String, Type>> result,
                                                           CursorQuad... quads) {
    List<Object[]> permutations = Lists.newArrayList();

    List<List<List<CursorQuad>>> partitions = ListPartitioner.partition(Lists.newArrayList(quads));
    for (List<List<CursorQuad>> partition : partitions) {
      permutations.add(new Object[]{partition, result});
    }

    return permutations;
  }

  private static List<Object[]> createPermutationsOfTestCase(Matcher<Map<String, Type>> result,
                                                             CursorQuad... quads) {
    List<Object[]> permutations = Lists.newArrayList();

    Collection<List<CursorQuad>> quadPerms = orderedPermutations(asList(quads), new CursorQuadComparator());
    quadPerms.forEach(quadPerm -> {
      List<List<List<CursorQuad>>> partitions = ListPartitioner.partition(quadPerm);
      for (List<List<CursorQuad>> partition : partitions) {
        permutations.add(new Object[]{partition, result});
      }
    });

    return permutations;
  }

  @Test
  public void runTest() throws Exception {
    BdbNonPersistentEnvironmentCreator dataStoreFactory = new BdbNonPersistentEnvironmentCreator();
    final BdbSchemaStore schema = new BdbSchemaStore(
      new BdbBackedData(dataStoreFactory.getDatabase(
        USER,
        DATA_SET,
        "schema",
        false,
        STRING_BINDING,
        STRING_BINDING,
        STRING_IS_CLEAN_HANDLER
      )),
      mock(ImportStatus.class)
    );

    try {
      final StoreUpdater storeUpdater = createInstance(dataStoreFactory, schema);

      for (List<CursorQuad> cursorQuadList : input) {
        storeUpdater.start(storeUpdater.getCurrentVersion() + 1);
        for (CursorQuad quad : cursorQuadList) {
          storeUpdater.onQuad(
            quad.getChangeType() == ASSERTED,
            quad.getSubject(),
            quad.getPredicate(),
            quad.getObject(),
            quad.getValuetype().orElse(null),
            quad.getLanguage().orElse(null),
            GRAPH
          );
        }
        storeUpdater.commit();
      }

      assertThat(schema.getStableTypes(), this.result);
    } finally {
      dataStoreFactory.close();
    }
  }

  private StoreUpdater createInstance(BdbNonPersistentEnvironmentCreator dataStoreFactory, BdbSchemaStore schema)
    throws DataStoreCreationException, nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.BdbDbCreationException,
    IOException {
    BdbTripleStore quadStore = new BdbTripleStore(dataStoreFactory.getDatabase(
      USER,
      DATA_SET,
      "rdfData",
      true,
      STRING_BINDING,
      STRING_BINDING,
      STRING_IS_CLEAN_HANDLER
    ));
    final BdbTypeNameStore typeNameStore = new BdbTypeNameStore(
      new BdbBackedData(dataStoreFactory.getDatabase(
        USER,
        DATA_SET,
        "typenames",
        false,
        STRING_BINDING,
        STRING_BINDING,
        STRING_IS_CLEAN_HANDLER
      )),
      GRAPH
    );

    final TupleBinding<Integer> integerBinding = TupleBinding.getPrimitiveBinding(Integer.class);
    final UpdatedPerPatchStore updatedPerPatchStore = new UpdatedPerPatchStore(
      dataStoreFactory.getDatabase(
        USER,
        DATA_SET,
        "updatedPerPatch",
        true,
        integerBinding,
        STRING_BINDING,
        new IsCleanHandler<Integer, String>() {
          @Override
          public Integer getKey() {
            return Integer.MAX_VALUE;
          }

          @Override
          public String getValue() {
            return "isClean";
          }
        }
      )
    );
    final BdbTruePatchStore truePatchStore = new BdbTruePatchStore(version ->
        dataStoreFactory.getDatabase(
            USER,
            DATA_SET,
            "truePatch" + version,
            true,
            STRING_BINDING,
            STRING_BINDING,
            STRING_IS_CLEAN_HANDLER
        ), updatedPerPatchStore
    );
    final BdbRmlDataSourceStore rmlDataSourceStore = new BdbRmlDataSourceStore(
      dataStoreFactory.getDatabase(
        USER,
        DATA_SET,
        "rmlSource",
        true,
        STRING_BINDING,
        STRING_BINDING,
        STRING_IS_CLEAN_HANDLER
      ),
      mock(ImportStatus.class)
    );
    VersionStore versionStore = new VersionStore(dataStoreFactory.getDatabase(
      USER,
      DATA_SET,
      "versions",
      false,
      STRING_BINDING,
      integerBinding,
      new IsCleanHandler<String, Integer>() {
        @Override
        public String getKey() {
          return "isClean";
        }

        @Override
        public Integer getValue() {
          return Integer.MAX_VALUE;
        }
      }
    ));

    return new StoreUpdater(
      quadStore,
      typeNameStore,
      truePatchStore,
      updatedPerPatchStore,
      Lists.newArrayList(schema, rmlDataSourceStore),
      versionStore,
      mock(ImportStatus.class)
    );
  }

  private static class CursorQuadComparator implements Comparator<CursorQuad> {
    @Override
    public int compare(CursorQuad o1, CursorQuad o2) {
      return o1.getSubject().compareTo(o2.getSubject()) +
        o1.getPredicate().compareTo(o2.getPredicate()) +
        o1.getObject().compareTo(o2.getObject()) +
        o1.getDirection().compareTo(o2.getDirection());
    }
  }
}
