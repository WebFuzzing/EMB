package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;

public class PropertyDescriptorFactory {
  private final PropertyParserFactory parserFactory;

  public PropertyDescriptorFactory(PropertyParserFactory parserFactory) {
    this.parserFactory = parserFactory;
  }

  public PropertyDescriptor getLocal(String propertyName, PropertyParser parser) {
    return new LocalPropertyDescriptor(propertyName, parser);
  }

  public PropertyDescriptor getLocal(String propertyName, Class<?> typeToParse) {
    return this.getLocal(propertyName, parserFactory.getParser(typeToParse));
  }

  public PropertyDescriptor getLocal(String propertyName, PropertyParser parser, String prefix, String postfix) {
    return new LocalPropertyDescriptor(propertyName, parser, prefix, postfix);
  }

  public PropertyDescriptor getLocal(String propertyName, Class<?> typeToConvert, String prefix, String postfix) {
    return new LocalPropertyDescriptor(propertyName, parserFactory.getParser(typeToConvert), prefix, postfix);
  }

  public PropertyDescriptor getLocal(String propertyName, String separator) {
    return new LocalPropertyDescriptor(propertyName, parserFactory.getJoinedListParser(separator));
  }

  public PropertyDescriptor getComposite(PropertyDescriptor preferred, PropertyDescriptor backUp) {
    return new CompositePropertyDescriptor(preferred, backUp);
  }

  private PropertyDescriptor getDerived(String relationName, String propertyName, String acceptedPropertyName,
                                        PropertyParser parser) {
    return new RelatedPropertyDescriptor(relationName, propertyName, acceptedPropertyName, parser);
  }

  public PropertyDescriptor getDerived(String relationName, String propertyName, String acceptedPropertyName,
                                       Class<?> typeToParse) {
    return this.getDerived(relationName, propertyName, acceptedPropertyName, parserFactory.getParser(typeToParse));

  }

  public PropertyDescriptor getDerivedWithSeparator(String relationName, String propertyName, PropertyParser parser,
                                                    String acceptedPropertyName, String separator) {
    return new RelatedPropertyDescriptor(relationName, propertyName, parser, acceptedPropertyName, separator);
  }

  public PropertyDescriptor getAppender(PropertyDescriptor first, PropertyDescriptor second, String separator) {
    return new AppenderPropertyDescriptor(first, second, separator);
  }


}
