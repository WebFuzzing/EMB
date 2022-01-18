package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import org.apache.tinkerpop.gremlin.structure.Vertex;


public class WwDocumentAuthorDescriptor implements PropertyDescriptor {

  private final PropertyDescriptorFactory propertyDescriptorFactory;
  private final PropertyParserFactory propertyParserFactory;
  private final PropertyDescriptor authorDescriptor;

  public WwDocumentAuthorDescriptor() {
    propertyParserFactory = new PropertyParserFactory();
    propertyDescriptorFactory = new PropertyDescriptorFactory(propertyParserFactory);
    authorDescriptor = createAuthorDescriptor();
  }

  private PropertyDescriptor createAuthorDescriptor() {
    PropertyDescriptor authorNameDescriptor = propertyDescriptorFactory.getDerivedWithSeparator(
            "isCreatedBy",
            "wwperson_names",
            propertyParserFactory.getParser(PersonNames.class),
            "wwrelation_accepted",
            "; ");
    PropertyDescriptor authorTempNameDescriptor = propertyDescriptorFactory.getDerivedWithSeparator(
            "isCreatedBy",
            "wwperson_tempName",
            propertyParserFactory.getParser(String.class),
            "wwrelation_accepted",
            "; ");

    return propertyDescriptorFactory
            .getComposite(authorNameDescriptor, authorTempNameDescriptor);
  }

  @Override
  public String get(Vertex vertex) {
    return authorDescriptor.get(vertex);
  }
}
