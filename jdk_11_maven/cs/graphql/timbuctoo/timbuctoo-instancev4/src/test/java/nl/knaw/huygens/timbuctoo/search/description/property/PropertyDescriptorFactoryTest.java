package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PropertyDescriptorFactoryTest {

  private PropertyDescriptorFactory instance;
  private PropertyParserFactory parserFactory;

  @Before
  public void setUp() throws Exception {
    parserFactory = mock(PropertyParserFactory.class);
    instance = new PropertyDescriptorFactory(parserFactory);
  }

  @Test
  public void getLocalWithPropertyNameAndParserReturnsALocalPropertyDescriptor() {
    PropertyParser parser = mock(PropertyParser.class);

    PropertyDescriptor descriptor = instance.getLocal("propertyName", parser);

    assertThat(descriptor, is(instanceOf(LocalPropertyDescriptor.class)));
  }

  @Test
  public void getLocalWithPrefixAndPostfixReturnsLocalPropertyDescriptor() {
    PropertyParser parser = mock(PropertyParser.class);

    PropertyDescriptor descriptor = instance.getLocal("propertyName", parser, "prefix", "postfix");

    assertThat(descriptor, is(instanceOf(LocalPropertyDescriptor.class)));
  }

  @Test
  public void getLocalLetsThePropertyParserFactoryCreateAPropertyParser() {
    instance.getLocal("propertyName", String.class);

    verify(parserFactory).getParser(String.class);
  }

  @Test
  public void getLocalWithPrefixAndPostfixLetsThePropertyParserFactoryCreateAPropertyParser() {
    instance.getLocal("propertyName", String.class, "prefix", "postfix");

    verify(parserFactory).getParser(String.class);
  }

  @Test
  public void getDerivedLetsThePropertyParserFactoryCreateAPropertyParser() {
    instance.getDerived("relationName", "propertyName", "accepted", String.class);

    verify(parserFactory).getParser(String.class);
  }

  @Test
  public void getDerivedWithSeparatorReturnsADerivedPropertyDescriptor() {
    PropertyParser parser = mock(PropertyParser.class);

    PropertyDescriptor descriptor = instance.getDerivedWithSeparator(
      "relationName",
      "propertyName",
      parser,
      "accepted",
      "--"
    );

    assertThat(descriptor, is(instanceOf(RelatedPropertyDescriptor.class)));
  }

  @Test
  public void getCompositeReturnsACompositePropertyDescriptor() {
    PropertyDescriptor descriptor =
      instance.getComposite(mock(PropertyDescriptor.class), mock(PropertyDescriptor.class));

    assertThat(descriptor, is(instanceOf(CompositePropertyDescriptor.class)));
  }

  @Test
  public void getAppenderReturnsAnAppenderPropertyDescriptor() {
    PropertyDescriptor descriptor =
      instance.getAppender(mock(PropertyDescriptor.class), mock(PropertyDescriptor.class), " ");

    assertThat(descriptor, is(instanceOf(AppenderPropertyDescriptor.class)));
  }

}
