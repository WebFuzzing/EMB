package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import nl.knaw.huygens.timbuctoo.model.properties.converters.Converter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Test;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PropertyTypesTest {

  @Test
  public void localPropertyReturnsLocalPropertyWithCorrectPropNameAndStringConverter() throws IOException {
    LocalProperty instance = localProperty("propName");
    Vertex vertex = mock(Vertex.class);
    VertexProperty vertexProperty = mock(VertexProperty.class);
    given(vertex.property("propName")).willReturn(vertexProperty);

    instance.setJson(vertex, null);
    instance.setJson(vertex, jsn("value"));

    verify(vertex, times(1)).property("propName", "value");
    verify(vertexProperty, times(1)).remove();
  }

  @Test
  public void localPropertyReturnsLocalPropertyWithCorrectPropNameAndCorrectConverter() throws IOException {
    Converter converter = mock(Converter.class);
    JsonNode value = jsn("value");
    given(converter.jsonToTinkerpop(value)).willReturn("converted-value");
    LocalProperty instance = localProperty("propName", converter);

    Vertex vertex = mock(Vertex.class);
    VertexProperty vertexProperty = mock(VertexProperty.class);
    given(vertex.property("propName")).willReturn(vertexProperty);

    instance.setJson(vertex, null);
    instance.setJson(vertex, value);

    verify(vertex, times(1)).property("propName", "converted-value");
    verify(vertexProperty, times(1)).remove();
  }
}
