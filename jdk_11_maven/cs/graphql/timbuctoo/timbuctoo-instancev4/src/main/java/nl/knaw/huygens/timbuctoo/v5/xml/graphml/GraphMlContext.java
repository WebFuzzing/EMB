package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class GraphMlContext {

  private final JAXBContext jaxbContext;

  public GraphMlContext() throws JAXBException {
    jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
  }

  public JAXBContext getJaxbContext() {
    return jaxbContext;
  }

  public Marshaller createMarshaller() throws JAXBException {
    return jaxbContext.createMarshaller();
  }

  public Unmarshaller createUnmarshaller() throws JAXBException {
    return jaxbContext.createUnmarshaller();
  }
}
