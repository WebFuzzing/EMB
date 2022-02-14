package nl.knaw.huygens.timbuctoo.databaselog;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLogOutput implements LogOutput {

  public static final Logger LOG = LoggerFactory.getLogger(FileLogOutput.class);
  private final ObjectMapper objectMapper;
  private final BufferedWriter writer;

  public FileLogOutput() {
    objectMapper = new ObjectMapper();
    try {
      writer = Files.newBufferedWriter(Paths.get("dblog"), Charset.forName("UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void prepareToWrite() {
    // nothing to do
  }

  @Override
  public void newVertex(Vertex vertex) {
    String modifiedString = vertex.value("modified");

    try {
      Change modified = objectMapper.readValue(modifiedString, Change.class);
      writeAndFlush(
        String.format("%d - Vertex with tim_id '%s' created.%n", modified.getTimeStamp(), vertex.value("tim_id")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateVertex(Vertex vertex) {
    String modifiedString = vertex.value("modified");

    try {
      Change modified = objectMapper.readValue(modifiedString, Change.class);
      writeAndFlush(
        String.format("%d - Vertex with tim_id '%s' updated.%n", modified.getTimeStamp(), vertex.value("tim_id")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void newEdge(Edge edge) {
    String modifiedString = edge.value("modified");
    try {
      Change modified = objectMapper.readValue(modifiedString, Change.class);
      writeAndFlush(
        String.format(
          "%d - Edge with tim_id '%s' between Vertex with tim_id '%s' and Vertex with tim_id '%s' created.%n",
          modified.getTimeStamp(),
          edge.value("tim_id"),
          edge.outVertex().value("tim_id"),
          edge.inVertex().value("tim_id")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateEdge(Edge edge) {
    String modifiedString = edge.value("modified");
    try {
      Change modified = objectMapper.readValue(modifiedString, Change.class);
      writeAndFlush(String.format(
        "%d - Edge with tim_id '%s' between Vertex with tim_id '%s' and Vertex with tim_id '%s' updated.%n",
        modified.getTimeStamp(),
        edge.value("tim_id"),
        edge.outVertex().value("tim_id"),
        edge.inVertex().value("tim_id")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void newProperty(Property property) {
    try {
      writeAndFlush(String.format("Property '%s' set to '%s'.%n", property.key(), property.value()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateProperty(Property property) {
    try {
      writeAndFlush(String.format("Property '%s' set to '%s'.%n", property.key(), property.value()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteProperty(String propertyName) {

    try {
      writeAndFlush(String.format("Property '%s' removed.%n", propertyName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void finishWriting() {
    // nothing to do
  }

  private void writeAndFlush(String format) throws IOException {
    writer.write(format);
    writer.flush();
  }

}
