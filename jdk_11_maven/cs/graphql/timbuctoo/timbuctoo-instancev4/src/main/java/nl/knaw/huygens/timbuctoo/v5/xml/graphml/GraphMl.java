package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(namespace = Gml.NAMESPACE, name = Gml.LOCAL_NAME)
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphMl {

  public static final QName QNAME = new QName(Gml.NAMESPACE, Gml.LOCAL_NAME);

  @XmlElement(name = Key.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Key> keyList = new ArrayList<>();

  @XmlElement(name = Graph.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Graph> graphList = new ArrayList<>();

  @XmlElement(name = Data.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Data> dataList = new ArrayList<>();

  public List<Key> getKeyList() {
    return keyList;
  }

  public void setKeyList(List<Key> keyList) {
    this.keyList = keyList;
  }

  public GraphMl addKey(Key key) {
    keyList.add(key);
    return this;
  }

  public List<Graph> getGraphList() {
    return graphList;
  }

  public void setGraphList(List<Graph> graphList) {
    this.graphList = graphList;
  }

  public GraphMl addGraph(Graph graph) {
    graphList.add(graph);
    return this;
  }

  public List<Data> getDataList() {
    return dataList;
  }

  public void setDataList(List<Data> dataList) {
    this.dataList = dataList;
  }

  public GraphMl addData(Data data) {
    dataList.add(data);
    return this;
  }

  public JAXBElement<GraphMl> asJaxbElement() {
    return new JAXBElement<GraphMl>(QNAME, GraphMl.class, this);
  }
}
