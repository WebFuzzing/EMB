package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class CompositePropertyDescriptorTest {

  @Test
  public void getReturnsTheValueOfTheFirstPropDescription() {
    PropertyDescriptor propertyDescriptor1 = mock(PropertyDescriptor.class);
    String valueOfDesc1 = "notNull";
    given(propertyDescriptor1.get(any(Vertex.class))).willReturn(valueOfDesc1);
    PropertyDescriptor propertyDescriptor2 = mock(PropertyDescriptor.class);
    CompositePropertyDescriptor instance = new CompositePropertyDescriptor(propertyDescriptor1, propertyDescriptor2);

    String value = instance.get(mock(Vertex.class));

    assertThat(value, is(valueOfDesc1));

    verify(propertyDescriptor1).get(any(Vertex.class));
    verifyNoInteractions(propertyDescriptor2);
  }

  @Test
  public void getReturnsTheValueOfTheSecondPropDescriptionIfTheValueOfTheFirstOneItIsNull() {
    PropertyDescriptor propertyDescriptor1 = mock(PropertyDescriptor.class);
    given(propertyDescriptor1.get(any(Vertex.class))).willReturn(null);
    PropertyDescriptor propertyDescriptor2 = mock(PropertyDescriptor.class);
    String valueOfDesc2 = "notNull";
    given(propertyDescriptor2.get(any(Vertex.class))).willReturn(valueOfDesc2);
    CompositePropertyDescriptor instance = new CompositePropertyDescriptor(propertyDescriptor1, propertyDescriptor2);

    String value = instance.get(mock(Vertex.class));

    assertThat(value, is(valueOfDesc2));

    verify(propertyDescriptor1).get(any(Vertex.class));
    verify(propertyDescriptor2).get(any(Vertex.class));
  }

  @Test
  public void getReturnsTheValueOfTheSecondPropDescriptionIfTheValueOfTheFirstOneIsAnEmptyString() {
    PropertyDescriptor propertyDescriptor1 = mock(PropertyDescriptor.class);
    given(propertyDescriptor1.get(any(Vertex.class))).willReturn("");
    PropertyDescriptor propertyDescriptor2 = mock(PropertyDescriptor.class);
    String valueOfDesc2 = "notNull";
    given(propertyDescriptor2.get(any(Vertex.class))).willReturn(valueOfDesc2);
    CompositePropertyDescriptor instance = new CompositePropertyDescriptor(propertyDescriptor1, propertyDescriptor2);

    String value = instance.get(mock(Vertex.class));

    assertThat(value, is(valueOfDesc2));

    verify(propertyDescriptor1).get(any(Vertex.class));
    verify(propertyDescriptor2).get(any(Vertex.class));
  }
}
