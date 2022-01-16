package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Graph {

  public static final String LOCAL_NAME = "graph";
  public static final QName QNAME = new QName(Gml.NAMESPACE, LOCAL_NAME);

  @XmlAttribute private String id;
  @XmlAttribute private String edgedefault = "directed";

  @XmlElement(name = Data.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Data> dataList = new ArrayList<>();

  @XmlElement(name = Node.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Node> nodeList = new ArrayList<>();

  @XmlElement(name = GmlEdge.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<GmlEdge> edgeList = new ArrayList<>();

  public Graph() {}

  public Graph(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEdgedefault() {
    return edgedefault;
  }

  public void setEdgedefault(String edgedefault) {
    this.edgedefault = edgedefault;
  }

  public List<Data> getDataList() {
    return dataList;
  }

  public void setDataList(List<Data> dataList) {
    this.dataList = dataList;
  }

  public Graph addData(Data data) {
    dataList.add(data);
    return this;
  }

  public List<Node> getNodeList() {
    return nodeList;
  }

  public void setNodeList(List<Node> nodeList) {
    this.nodeList = nodeList;
  }

  public Graph addNode(Node node) {
    nodeList.add(node);
    return this;
  }

  public List<GmlEdge> getEdgeList() {
    return edgeList;
  }

  public void setEdgeList(List<GmlEdge> edgeList) {
    this.edgeList = edgeList;
  }

  public Graph addEdge(GmlEdge edge) {
    edgeList.add(edge);
    return this;
  }

  public JAXBElement<Graph> asJaxbElement() {
    return new JAXBElement<Graph>(QNAME, Graph.class, this);
  }
}
