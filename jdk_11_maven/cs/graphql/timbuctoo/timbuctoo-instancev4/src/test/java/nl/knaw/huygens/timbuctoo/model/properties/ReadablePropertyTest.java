package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.properties.converters.StringToUnencodedStringOfLimitedValuesConverter;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty.CLIENT_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty.DATABASE_LABEL;
import static nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty.PROPERTY_TYPE_NAME;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ReadablePropertyTest {
  private Graph graph;

  @Before
  public void setUp() {
    graph = newGraph().build();
  }

  @Test
  public void saveCreatesANewVertexWithCorrectLabelAndProperties() {
    ReadableProperty readableProperty = new ReadableProperty(null, null) {
      @Override
      public String getTypeId() {
        return null;
      }

      @Override
      public String getUniqueTypeId() {
        return "type-id";
      }
    };

    Vertex result = readableProperty.save(graph, "client-prop-name");

    assertThat(result, likeVertex()
      .withProperty(CLIENT_PROPERTY_NAME, "client-prop-name")
      .withProperty(PROPERTY_TYPE_NAME, "type-id")
      .withLabel(DATABASE_LABEL)
    );
  }

  @Test
  public void loadLoadsAPropertyConfiguration()
    throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    IOException {

    final String[] options = {"a", "b"};
    final Vertex vertex = graph.addVertex(DATABASE_LABEL);
    final String typeIdentifier = new StringToUnencodedStringOfLimitedValuesConverter(new String[]{})
      .getUniqueTypeIdentifier();
    vertex.property(CLIENT_PROPERTY_NAME, "clientName");
    vertex.property(PROPERTY_TYPE_NAME, typeIdentifier);
    vertex.property(LocalProperty.OPTIONS_PROPERTY_NAME, new ObjectMapper().writeValueAsString(options));
    vertex.property(LocalProperty.DATABASE_PROPERTY_NAME, "dbName");

    ReadableProperty result = ReadableProperty.load(vertex, null);

    assertThat(result, instanceOf(LocalProperty.class));
    assertThat(result.getUniqueTypeId(), equalTo(typeIdentifier));
    assertThat(((LocalProperty) result).getOptions().get(), contains("a", "b"));
  }

  @Test
  public void loadLoadsTheCustomDisplayNameProperties()
    throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    IOException {
    final Vertex documentDisplayNameVertex = graph.addVertex(DATABASE_LABEL);
    final Vertex personDisplayNameVertex = graph.addVertex(DATABASE_LABEL);
    final Vertex rdfDisplayNameVertex = graph.addVertex(DATABASE_LABEL);
    final String documentDisplayNameTypeId = new WwDocumentDisplayName().getUniqueTypeId();
    final String personDisplayNameTypeId = new WwPersonDisplayName().getUniqueTypeId();
    final String rdfDisplayNameTypeId = new RdfImportedDefaultDisplayname().getUniqueTypeId();
    documentDisplayNameVertex.property(CLIENT_PROPERTY_NAME, "clientName");
    documentDisplayNameVertex.property(PROPERTY_TYPE_NAME, documentDisplayNameTypeId);
    personDisplayNameVertex.property(CLIENT_PROPERTY_NAME, "clientName2");
    personDisplayNameVertex.property(PROPERTY_TYPE_NAME, personDisplayNameTypeId);
    rdfDisplayNameVertex.property(CLIENT_PROPERTY_NAME, "@displayName");
    rdfDisplayNameVertex.property(PROPERTY_TYPE_NAME, rdfDisplayNameTypeId);

    ReadableProperty documentResult = ReadableProperty.load(documentDisplayNameVertex, null);
    ReadableProperty personResult = ReadableProperty.load(personDisplayNameVertex, null);
    ReadableProperty rdfResult = ReadableProperty.load(rdfDisplayNameVertex, null);

    assertThat(documentResult, instanceOf(WwDocumentDisplayName.class));
    assertThat(personResult, instanceOf(WwPersonDisplayName.class));
    assertThat(rdfResult, instanceOf(RdfImportedDefaultDisplayname.class));
  }

  @Test(expected = IOException.class)
  public void loadThrowsWhenTypeIsNotSupported()
    throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException,
    IllegalAccessException {

    final Vertex vertex = graph.addVertex(DATABASE_LABEL);
    final String typeIdentifier = new StringToUnencodedStringOfLimitedValuesConverter(new String[]{})
      .getUniqueTypeIdentifier();
    vertex.property(CLIENT_PROPERTY_NAME, "clientName");
    vertex.property(PROPERTY_TYPE_NAME, typeIdentifier);

    ReadableProperty.load(vertex, null);
  }
}
