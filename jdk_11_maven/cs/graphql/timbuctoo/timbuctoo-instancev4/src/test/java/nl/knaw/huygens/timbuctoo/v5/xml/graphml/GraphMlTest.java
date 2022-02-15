package nl.knaw.huygens.timbuctoo.v5.xml.graphml;


import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created on 2017-06-01 16:24.
 */
public class GraphMlTest {

  @Test
  public void validate() throws Exception {
    GraphMl graphMl = creategraphml();
    JAXBContext jc = new GraphMlContext().getJaxbContext();
    JAXBSource source = new JAXBSource(jc, graphMl);

    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(new URL("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd"));

    Validator validator = schema.newValidator();
    validator.setErrorHandler(new GraphErrorHandler());
    validator.validate(source);
  }

  @Test
  public void testMarshalUnmarshalGraphMl() throws Exception {
    GraphMl graphMl = creategraphml();
    JAXBContext jaxbContext = JAXBContext.newInstance(GraphMl.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    //jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter writer = new StringWriter();

    jaxbMarshaller.marshal(graphMl, writer);
    String xml1 = writer.toString();
    //System.out.println(xml1);

    InputStream input1 = IOUtils.toInputStream(xml1);
    Unmarshaller unma = jaxbContext.createUnmarshaller();
    GraphMl graphMl1 = (GraphMl) unma.unmarshal(input1);

    writer = new StringWriter();
    jaxbMarshaller.marshal(graphMl1, writer);
    String xml2 = writer.toString();
    assertThat(xml1, equalTo(xml2));
  }

  @Test
  public void testSreaming() throws Exception {
    OutputStream out = new ByteArrayOutputStream();
    XMLOutputFactory xof = XMLOutputFactory.newFactory();
    XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(out, StandardCharsets.UTF_8.name()));
    xsw.setDefaultNamespace("http://graphml.graphdrawing.org/xmlns");

    xsw.writeStartDocument("UTF-8", "1.0");
    xsw.writeStartElement("http://graphml.graphdrawing.org/xmlns","graphml");
    xsw.writeDefaultNamespace("http://graphml.graphdrawing.org/xmlns");

    GraphMlContext graphMlContext = new GraphMlContext();
    Marshaller jaxbMarshaller = graphMlContext.createMarshaller();
    //jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

    for (int i = 0; i < 5; i++) {
      JAXBElement<Key> je = new Key("d" + i).withAttrName("r").withAttrType("int").withFor("edge").asJaxbElement();
      jaxbMarshaller.marshal(je, xsw);
    }

    xsw.writeStartElement("http://graphml.graphdrawing.org/xmlns","graph");
    xsw.writeAttribute("edgedefault", "directed");

    for (int h = 0; h < 3; h++) {

      Node node = new Node("n" + h);
      for (int i = 0; i < 5; i++) {
        node.addData(new Data().withKey("d" + i).withValue("42"));
      }
      jaxbMarshaller.marshal(node.asJaxbElement(), xsw);
    }

    xsw.writeEndElement();
    xsw.writeEndElement();

    xsw.writeEndDocument();
    xsw.close();
    //System.out.println(out.toString());
  }

  private GraphMl creategraphml() {
    GraphMl graphMl = new GraphMl()
      .addKey(new Key().withId("d1").withFor("node").withAttrName("label").withAttrType("string"))
      .addKey(new Key().withId("d2").withFor("edge").withAttrName("weight").withAttrType("double"))
      .addKey(new Key().withId("d3").withFor("edge").withAttrName("foo").withAttrType("double")
      );
    Graph graph1 = new Graph("g1")
      .addNode(new Node("n1").addData(new Data().withKey("d1").withValue("blauw")))
      .addNode(new Node("n2").addData(new Data().withKey("d2").withValue("groen")))
      .addNode(new Node("n3").addData(new Data().withKey("d2").withValue("rood")))
      .addEdge(new GmlEdge("e1").withSource("n1").withTarget("n2")
                                .addData(new Data().withKey("d3").withValue("1.0"))
                                .addData(new Data().withKey("d2").withValue("geel")))
      .addEdge(new GmlEdge("e2").withSource("n2").withTarget("n3"));

    graphMl.addGraph(graph1);
    return graphMl;
  }

  private static class GraphErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      throw exception;
    }
  }
}
