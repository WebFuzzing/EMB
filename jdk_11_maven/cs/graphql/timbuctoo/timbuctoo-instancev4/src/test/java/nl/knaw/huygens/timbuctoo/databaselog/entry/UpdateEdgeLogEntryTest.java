package nl.knaw.huygens.timbuctoo.databaselog.entry;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.junit.Test;

import java.util.Set;

import static nl.knaw.huygens.timbuctoo.util.EdgeMockBuilder.edge;
import static nl.knaw.huygens.timbuctoo.util.PropertyMatcher.likeProperty;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UpdateEdgeLogEntryTest {

  public static final long TIMESTAMP = 1000L;
  public static final String ID = "id";

  @Test
  public void appendToLogLogsTheEdgeIsUpdated() {
    Edge edge = edge().withProperty("rev", 1).build();
    Edge prevEdge = edge().withProperty("rev", 2).build();
    LogOutput logOutput = mock(LogOutput.class);
    UpdateEdgeLogEntry instance = new UpdateEdgeLogEntry(edge, prevEdge);

    instance.appendToLog(logOutput);

    verify(logOutput).updateEdge(edge);
  }

  @Test
  public void appendToLogAddsANewPropertyLineForEachPropertyThatDoesNotExistInThePreviousVersion() {
    Edge prevEdge = edge().withId(ID).withProperty("oldProp", "oldValue").withProperty("rev", 1).build();
    String newProp = "newProp";
    String newProp2 = "newProp2";
    Edge edge = edge().withId(ID)
                      .withProperty(newProp, "value")
                      .withProperty(newProp2, "value")
                      .withProperty("oldProp", "oldValue")
                      .withProperty("rev", 2)
                      .build();
    LogOutput dbLog = mock(LogOutput.class);
    UpdateEdgeLogEntry instance = new UpdateEdgeLogEntry(edge, prevEdge);

    instance.appendToLog(dbLog);

    verify(dbLog).updateEdge(edge);
    verify(dbLog).newProperty(argThat(likeProperty().withKey(newProp)));
    verify(dbLog).newProperty(argThat(likeProperty().withKey(newProp2)));
    verify(dbLog).updateProperty(argThat(likeProperty().withKey("rev")));
    verifyNoMoreInteractions(dbLog);
  }

  @Test
  public void appendToLogAddsAnUpdatePropertyLineForEachPropertyThatHasADifferentValueInTheUpdatedVersion() {
    String updatedProp = "updatedProp";
    Edge prevEdge = edge().withId(ID)
                          .withProperty(updatedProp, "oldValue")
                          .withProperty("oldProp", "oldValue")
                          .withProperty("rev", 1)
                          .build();
    Edge edge = edge().withId(ID)
                      .withProperty(updatedProp, "newValue")
                      .withProperty("oldProp", "oldValue")
                      .withProperty("rev", 2)
                      .build();
    LogOutput dbLog = mock(LogOutput.class);
    UpdateEdgeLogEntry instance = new UpdateEdgeLogEntry(edge, prevEdge);

    instance.appendToLog(dbLog);

    verify(dbLog).updateEdge(edge);
    verify(dbLog).updateProperty(argThat(likeProperty().withKey(updatedProp)));
    verify(dbLog).updateProperty(argThat(likeProperty().withKey("rev")));
    verifyNoMoreInteractions(dbLog);
  }

  @Test
  public void appendToLogAddsAnDeletePropertyLineForEachPropertyThatDoesNotExistInTheNewVersion() {
    String deletedProp = "deletedProp";
    Edge prevEdge = edge().withId(ID)
                          .withProperty(deletedProp, "oldValue")
                          .withProperty("oldProp", "oldValue")
                          .withProperty("rev", 1)
                          .build();
    Edge edge = edge().withId(ID)
                      .withProperty("oldProp", "oldValue")
                      .withProperty("rev", 2)
                      .build();
    LogOutput dbLog = mock(LogOutput.class);
    UpdateEdgeLogEntry instance = new UpdateEdgeLogEntry(edge, prevEdge);

    instance.appendToLog(dbLog);

    verify(dbLog).updateEdge(edge);
    verify(dbLog).deleteProperty(deletedProp);
    verify(dbLog).updateProperty(argThat(likeProperty().withKey("rev")));
    verifyNoMoreInteractions(dbLog);
  }

  @Test
  public void appendToLogIgnoresThePropertiesToIgnore() {
    String deletedPropToIgnore = "deletedPropToIgnore";
    String updatedPropToIgnore = "updatedPropToIgnore";
    String newPropToIgnore = "newPropToIgnore";
    Set<String> propertiesToIgnore = Sets.newHashSet(deletedPropToIgnore, updatedPropToIgnore, newPropToIgnore);
    Edge prevEdge = edge().withId(ID)
                          .withProperty(deletedPropToIgnore, "oldValue")
                          .withProperty(updatedPropToIgnore, "oldValue")
                          .withProperty("rev", 1)
                          .build();
    Edge edge = edge().withId(ID)
                      .withProperty(updatedPropToIgnore, "oldValue")
                      .withProperty(newPropToIgnore, "value")
                      .withProperty("rev", 2)
                      .build();
    LogOutput dbLog = mock(LogOutput.class);
    UpdateEdgeLogEntry instance = new UpdateEdgeLogEntry(edge, prevEdge, propertiesToIgnore);

    instance.appendToLog(dbLog);

    verify(dbLog).updateEdge(edge);
    verify(dbLog).updateProperty(argThat(likeProperty().withKey("rev")));
    verifyNoMoreInteractions(dbLog);
  }

}
