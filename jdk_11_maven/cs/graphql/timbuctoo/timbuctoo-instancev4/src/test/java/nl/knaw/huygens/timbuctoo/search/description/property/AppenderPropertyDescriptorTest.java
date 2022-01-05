package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class AppenderPropertyDescriptorTest {

  @Test
  public void getReturnAppendsTheValuesOfThePropertyDescriptorsSeparatedByTheSeparator() {
    PropertyDescriptor propertyDescriptor1 = propertyDescriptorThatReturns("value1");
    PropertyDescriptor propertyDescriptor2 = propertyDescriptorThatReturns("value2");
    String separator = " ";
    AppenderPropertyDescriptor instance = new AppenderPropertyDescriptor(propertyDescriptor1, propertyDescriptor2,
      separator);
    Vertex vertex = vertex().build();

    String value = instance.get(vertex);

    assertThat(value, is("value1 value2"));
  }

  @Test
  public void getReturnsOnlyTheValueOfTheFirstDescriptorIfTheSecondReturnsNull() {
    PropertyDescriptor propertyDescriptor1 = propertyDescriptorThatReturns("value1");
    PropertyDescriptor propertyDescriptor2 = propertyDescriptorThatReturns(null);
    String separator = " ";
    AppenderPropertyDescriptor instance = new AppenderPropertyDescriptor(propertyDescriptor1, propertyDescriptor2,
      separator);
    Vertex vertex = vertex().build();

    String value = instance.get(vertex);

    assertThat(value, is("value1"));
  }

  @Test
  public void getReturnsOnlyTheValueOfTheSecondDescriptorIfTheFirstReturnsNull() {
    PropertyDescriptor propertyDescriptor1 = propertyDescriptorThatReturns(null);
    PropertyDescriptor propertyDescriptor2 = propertyDescriptorThatReturns("value2");
    String separator = " ";
    AppenderPropertyDescriptor instance = new AppenderPropertyDescriptor(propertyDescriptor1, propertyDescriptor2,
      separator);
    Vertex vertex = vertex().build();

    String value = instance.get(vertex);

    assertThat(value, is("value2"));
  }

  private PropertyDescriptor propertyDescriptorThatReturns(String value) {
    PropertyDescriptor propertyDescriptor1 = mock(PropertyDescriptor.class);
    given(propertyDescriptor1.get(any())).willReturn(value);
    return propertyDescriptor1;
  }


}
