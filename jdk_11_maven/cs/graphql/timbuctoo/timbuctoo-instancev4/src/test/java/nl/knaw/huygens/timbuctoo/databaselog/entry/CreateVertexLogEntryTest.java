package nl.knaw.huygens.timbuctoo.databaselog.entry;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.mockito.InOrder;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static nl.knaw.huygens.timbuctoo.util.PropertyMatcher.likeProperty;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CreateVertexLogEntryTest {

  @Test
  public void appendToLogAddsNewVertexLineForAVertexWithoutPreviousVersions() {
    Vertex vertex = vertex().build();
    LogOutput dbLog = mock(LogOutput.class);
    CreateVertexLogEntry   instance = new CreateVertexLogEntry(vertex);

    instance.appendToLog(dbLog);

    verify(dbLog).newVertex(vertex);
  }

  @Test
  public void appendToLogAddsANewPropertyLineForEachNewPropertyAfter() {
    String key1 = "property1";
    String key2 = "property2";
    String key3 = "property3";
    Vertex vertex = vertex()
      .withProperty(key1, "value")
      .withProperty(key2, "value")
      .withProperty(key3, "value")
      .build();
    LogOutput dbLog = mock(LogOutput.class);
    CreateVertexLogEntry instance = new CreateVertexLogEntry(vertex);

    instance.appendToLog(dbLog);

    verify(dbLog).newProperty(argThat(likeProperty().withKey(key1)));
    verify(dbLog).newProperty(argThat(likeProperty().withKey(key2)));
    verify(dbLog).newProperty(argThat(likeProperty().withKey(key3)));
  }

  @Test
  public void appendToLogFirstAddsNewVertexThanTheNewProperties() {
    String key1 = "property1";

    Vertex vertex = vertex()
      .withProperty(key1, "value")
      .build();
    LogOutput dbLog = mock(LogOutput.class);
    CreateVertexLogEntry vertexLogEntry = new CreateVertexLogEntry(vertex);

    vertexLogEntry.appendToLog(dbLog);

    InOrder inOrder = inOrder(dbLog);
    inOrder.verify(dbLog).newVertex(vertex);
    inOrder.verify(dbLog).newProperty(argThat(likeProperty().withKey(key1)));
  }

  @Test
  public void appendToLogIgnoresThePropertiesToIgnore() {
    LogOutput logOutput = mock(LogOutput.class);
    String propToIgnore = "propToIgnore";
    Vertex vertex = vertex().withProperty(propToIgnore, "value").build();
    CreateVertexLogEntry instance = new CreateVertexLogEntry(vertex, Sets.newHashSet(propToIgnore));

    instance.appendToLog(logOutput);

    verify(logOutput, never()).newProperty(argThat(likeProperty().withKey(propToIgnore)));
  }

}
