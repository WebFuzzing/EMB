package nl.knaw.huygens.timbuctoo.server.healthchecks;

import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import nl.knaw.huygens.timbuctoo.util.TestGraphBuilder;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.mockito.InOrder;
import org.neo4j.graphdb.GraphDatabaseService;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class DatabaseValidatorTest {

  @Test
  public void checkCallsInitAndFinish() {
    TinkerPopGraphManager graph = makeGraph("1");
    DatabaseCheck databaseCheck1 = mock(DatabaseCheck.class);
    DatabaseCheck databaseCheck2 = mock(DatabaseCheck.class);

    DatabaseValidator instance =
      new DatabaseValidator(graph, databaseCheck1, databaseCheck2);

    instance.check();

    InOrder inOrder = inOrder(databaseCheck1, databaseCheck2);
    inOrder.verify(databaseCheck1).init(any(Graph.class), any(GraphDatabaseService.class));
    inOrder.verify(databaseCheck2).init(any(Graph.class), any(GraphDatabaseService.class));
    inOrder.verify(databaseCheck1).check(any(Vertex.class));
    inOrder.verify(databaseCheck2).check(any(Vertex.class));
    inOrder.verify(databaseCheck1).finish();
    inOrder.verify(databaseCheck2).finish();
  }


  @Test
  public void checkRunsAllTheDatabaseChecksForEachVertex() {
    String id1 = "id1";
    String id2 = "id2";
    TinkerPopGraphManager graph = makeGraph(id1, id2);
    DatabaseCheck databaseCheck1 = mock(DatabaseCheck.class);
    DatabaseCheck databaseCheck2 = mock(DatabaseCheck.class);

    DatabaseValidator instance =
      new DatabaseValidator(graph, databaseCheck1, databaseCheck2);

    instance.check();

    verify(databaseCheck1).check(argThat(likeVertex().withTimId(id1)));
    verify(databaseCheck1).check(argThat(likeVertex().withTimId(id2)));
    verify(databaseCheck2).check(argThat(likeVertex().withTimId(id1)));
    verify(databaseCheck2).check(argThat(likeVertex().withTimId(id2)));
  }

  @Test
  public void checkReturnsTheValidationResult() {
    TinkerPopGraphManager graph = makeGraph("id1", "id2");
    DatabaseValidator instance = new DatabaseValidator(graph);

    ValidationResult result = instance.check();

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void checkReturnsANonValidValidationResultWhenAtLeastOneOfTheChecksFails() {
    TinkerPopGraphManager graph = makeGraph("id1", "id2");
    DatabaseValidator instance = new DatabaseValidator(graph, nonValidDatabaseCheck());

    ValidationResult validationResult = instance.check();

    assertThat(validationResult.isValid(), is(false));
  }

  @Test
  public void checkReturnsAValidValidationResultWhenAllOfTheChecksSucceed() {
    TinkerPopGraphManager graph = makeGraph("id", "id2");
    DatabaseValidator instance = new DatabaseValidator(graph, validDatabaseCheck());

    ValidationResult result = instance.check();

    assertThat(result.isValid(), is(true));
  }

  private DatabaseCheck validDatabaseCheck() {
    return createDatabaseCheck(true);
  }

  private DatabaseCheck nonValidDatabaseCheck() {
    return createDatabaseCheck(false);
  }

  private DatabaseCheck createDatabaseCheck(final boolean isValid) {
    DatabaseCheck databaseCheck = mock(DatabaseCheck.class);
    given(databaseCheck.check(any(Vertex.class))).willReturn(new ValidationResult() {
      @Override
      public boolean isValid() {
        return isValid;
      }

      @Override
      public String getMessage() {
        throw new UnsupportedOperationException("Not implemented yet");
      }
    });
    return databaseCheck;
  }

  private TinkerPopGraphManager makeGraph(String... ids) {
    TestGraphBuilder graphBuilder = newGraph();
    for (String id : ids) {
      graphBuilder.withVertex(vertex -> vertex.withTimId(id));
    }
    return graphBuilder.wrap();
  }
}
