package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import nl.knaw.huygens.timbuctoo.util.TestGraphBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DutchCaribbeanArchiveAndArchiverPeriodFacetDescriptionTest {

  public static final String FACET_NAME = "facetName";
  public static final String BEGIN_YEAR = "beginYear";
  public static final String END_YEAR = "endYear";
  private DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance;

  @Before
  public void setUp() throws Exception {
    instance = new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR);
  }

  private GraphTraversal<Vertex, Vertex> makeGraph(YearSpan... years) {
    final TestGraphBuilder testGraphBuilder = newGraph();
    for (YearSpan year : years) {
      testGraphBuilder.withVertex(v -> {
        v.withTimId(year.getId());
        if (year.getBeginYear() != null) {
          v.withProperty(BEGIN_YEAR, year.getBeginYear());
        }
        if (year.getEndYear() != null) {
          v.withProperty(END_YEAR, year.getEndYear());
        }
      });
    }
    return testGraphBuilder.build().traversal().V();
  }

  private void shouldContainJustRight(GraphTraversal<Vertex, Vertex> traversal, long startYear, long endYear) {
    List<FacetValue> facetValues = Lists.newArrayList(
      new DateRangeFacetValue(FACET_NAME, startYear * 10_000, endYear * 10_000)
    );

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), contains(
      likeVertex().withTimId("just_right")
    ));
  }

  private void shouldContainNothing(GraphTraversal<Vertex, Vertex> traversal, long startYear, long endYear) {
    List<FacetValue> facetValues = Lists.newArrayList(
      new DateRangeFacetValue(FACET_NAME, startYear * 10_000, endYear * 10_000)
    );

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), empty());
  }

  @Test
  public void addsNoFilterWhenTheFacetIsNotPresent() {
    GraphTraversal<Vertex, Vertex> traversal = makeGraph(
      new YearSpan("too_soon", 1800, 1850),
      new YearSpan("too_late", 2000, 2050),
      new YearSpan("just_right", 1900, 1950)
    );
    List<FacetValue> facetValues = Lists.newArrayList();

    instance.filter(traversal, facetValues);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("too_soon"),
      likeVertex().withTimId("too_late"),
      likeVertex().withTimId("just_right")
    ));
  }

  /*
   *                                         Vs--Ve  //too_late   2000 - 2050
   * Vs--Ve                                          //too_soon   1800 - 1850
   *              Vs------------Ve                   //Just_right 1900 - 1950
   *
   *
   *         Qs-------Qe                             //partialBefore
   *                        Qs----------Qe           //partialAfter
   *                  Qs----Qe                       //inside
   *         Qs-------------------------Qe           //outside
   * match when: Qs < Ve && Qe > Vs
   */

  private GraphTraversal<Vertex, Vertex> basicGraph() {
    return makeGraph(
      new YearSpan("too_soon", 1800, 1850),
      new YearSpan("too_late", 2000, 2050),
      new YearSpan("just_right", 1900, 1950)
    );
  }

  @Test
  public void filterPartialBefore() {
    shouldContainJustRight(basicGraph(), 1890, 1925);
  }

  @Test
  public void filterPartialAfter() {
    shouldContainJustRight(basicGraph(), 1925, 1975);
  }

  @Test
  public void filterInside() {
    shouldContainJustRight(basicGraph(), 1910, 1940);
  }

  @Test
  public void filterOutside() {
    shouldContainJustRight(basicGraph(), 1890, 1960);
  }

  @Test
  public void startEdgeInclusive() {
    shouldContainJustRight(basicGraph(), 1900, 1900);
  }

  @Test
  public void endEdgeInclusive() {
    shouldContainJustRight(basicGraph(), 1950, 1950);
  }

  @Test
  public void ignoresYearsWithTrailingIsoDates() {
    shouldContainJustRight(makeGraph(
      new YearSpan("just_right", "1900-01-01", "1950-12-31")
    ), 1940, 1960);
  }

  @Test
  public void ignoresVerticesWithIncorrectDates() {
    shouldContainNothing(makeGraph(
      new YearSpan("will_not_be_found", "It's not unusual to be loved by anyone", "1950")
    ), 1940, 1960);
  }

  @Test
  public void ignoresOpenEndedVertices() {
    shouldContainNothing(makeGraph(
      new YearSpan("will_not_be_found", "1950", null)
    ), 1940, 1960);
    shouldContainNothing(makeGraph(
      new YearSpan("will_not_be_found", null, "1950")
    ), 1940, 1960);
  }

  @Test
  public void worksWithYearsBeforeTHousand() {
    shouldContainJustRight(makeGraph(
      new YearSpan("just_right", 300, 350)
    ), 250, 350);
  }

  @Test
  public void doesNotWorkWithYearsBeforeChrist() {
    shouldContainNothing(makeGraph(
      new YearSpan("not_found", -4000, -3050)
    ), -3060, -3020);
  }

  @Test
  public void getValuesReturnsTheBeginPropertyAndEndPropertyOfAVertex() {
    String beginYear = "1900";
    String endYear = "2000";
    Vertex vertex = vertex().withProperty(BEGIN_YEAR, beginYear).withProperty(END_YEAR, endYear).build();

    DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance =
      new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR);

    List<String> values = instance.getValues(vertex);

    assertThat(values, containsInAnyOrder(beginYear, endYear));
  }

  @Test
  public void getValuesReturnsTheBeginDateIfTheEndDateIsNull() {
    String beginYear = "1900";
    Vertex vertex = vertex().withProperty(BEGIN_YEAR, beginYear).build();

    DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance =
      new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR);

    List<String> values = instance.getValues(vertex);

    assertThat(values, contains(beginYear));
  }

  @Test
  public void getValuesReturnsTheEndDateIfTheBeginDateIsNull() {
    String endYear = "1900";
    Vertex vertex = vertex().withProperty(END_YEAR, endYear).build();

    DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance =
      new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR);

    List<String> values = instance.getValues(vertex);

    assertThat(values, contains(endYear));
  }

  @Test
  public void getValuesReturnsAnEmptyListIfTheBeginDateAndTheEndDateAreNull() {
    Vertex vertex = vertex().build();

    DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance =
      new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR);

    List<String> values = instance.getValues(vertex);

    assertThat(values, is(empty()));
  }

  @Test
  public void getFacetDelegatesToTheFacetGetter() {
    FacetGetter facetGetter = mock(FacetGetter.class);
    HashMap<String, Set<Vertex>> values = Maps.newHashMap();
    Facet facet = mock(Facet.class);
    given(facetGetter.getFacet(FACET_NAME, values)).willReturn(facet);
    DutchCaribbeanArchiveAndArchiverPeriodFacetDescription instance =
      new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(FACET_NAME, BEGIN_YEAR, END_YEAR, facetGetter);

    Facet actualFacet = instance.getFacet(values);

    assertThat(actualFacet, is(sameInstance(facet)));
    verify(facetGetter).getFacet(FACET_NAME, values);
  }

  private class YearSpan {
    private final String id;
    private final String beginYear;
    private final String endYear;

    public YearSpan(String id, int beginYear, int endYear) {
      this.id = id;
      this.beginYear = beginYear + "";
      this.endYear = endYear + "";
    }

    public YearSpan(String id, String beginYear, String endYear) {
      this.id = id;
      this.beginYear = beginYear;
      this.endYear = endYear;
    }

    public String getId() {
      return id;
    }

    public String getBeginYear() {
      return beginYear;
    }

    public String getEndYear() {
      return endYear;
    }
  }
}
