package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

  public ObjectFactory() {}

  public Data createData() {
    return new Data();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = Data.LOCAL_NAME)
  public JAXBElement<Data> createData(Data value) {
    return new JAXBElement<Data>(Data.QNAME, Data.class, value);
  }

  public GmlEdge createEdge() {
    return new GmlEdge();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = GmlEdge.LOCAL_NAME)
  public JAXBElement<GmlEdge> createEdge(GmlEdge value) {
    return new JAXBElement<GmlEdge>(GmlEdge.QNAME, GmlEdge.class, value);
  }

  public Graph createGraph() {
    return new Graph();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = Graph.LOCAL_NAME)
  public JAXBElement<Graph> createGraph(Graph value) {
    return new JAXBElement<Graph>(Graph.QNAME, Graph.class, value);
  }

  public GraphMl createGraphMl() {
    return new GraphMl();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = Gml.LOCAL_NAME)
  public JAXBElement<GraphMl> createGraphMl(GraphMl value) {
    return new JAXBElement<GraphMl>(GraphMl.QNAME, GraphMl.class,null, value);
  }

  public Key createKey() {
    return new Key();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = Key.LOCAL_NAME)
  public JAXBElement<Key> createKey(Key value) {
    return new JAXBElement<Key>(Key.QNAME, Key.class, GraphMl.class, value);
  }

  public Node createNode() {
    return new Node();
  }

  @XmlElementDecl(namespace = Gml.NAMESPACE, name = Node.LOCAL_NAME)
  public JAXBElement<Node> createNode(Node value) {
    return new JAXBElement<Node>(Node.QNAME, Node.class, value);
  }


}
