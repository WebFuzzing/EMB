package nl.knaw.huygens.timbuctoo.search.description.property;


import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class WwDocumentDisplayNameDescriptor implements PropertyDescriptor {

  private final PropertyDescriptorFactory propertyDescriptorFactory;
  private final PropertyParserFactory propertyParserFactory;
  private final PropertyDescriptor displayNameDescriptor;

  public WwDocumentDisplayNameDescriptor() {
    propertyParserFactory = new PropertyParserFactory();
    propertyDescriptorFactory = new PropertyDescriptorFactory(propertyParserFactory);
    displayNameDescriptor = createDisplayNameDescriptor();
  }

  private PropertyDescriptor createDisplayNameDescriptor() {
    PropertyDescriptor titleDescriptor = propertyDescriptorFactory.getLocal("wwdocument_title", String.class);
    PropertyDescriptor dateDescriptor = propertyDescriptorFactory.getLocal("wwdocument_date", Datable.class, "(", ")");

    PropertyDescriptor documentDescriptor = propertyDescriptorFactory.getAppender(titleDescriptor, dateDescriptor, " ");

    return propertyDescriptorFactory.getAppender(new WwDocumentAuthorDescriptor(), documentDescriptor, " - ");
  }


  @Override
  public String get(Vertex vertex) {
    return displayNameDescriptor.get(vertex);
  }
}
