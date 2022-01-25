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
public class GmlEdge {

  public static final String LOCAL_NAME = "edge";
  public static final QName QNAME = new QName(Gml.NAMESPACE, LOCAL_NAME);

  @XmlAttribute() private String id;
  @XmlAttribute() private String source;
  @XmlAttribute() private String target;

  @XmlElement(name = Data.LOCAL_NAME, namespace = Gml.NAMESPACE)
  private List<Data> dataList = new ArrayList<>();

  public GmlEdge() {}

  public GmlEdge(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getSource() {
    return source;
  }

  public GmlEdge withSource(String source) {
    this.source = source;
    return this;
  }

  public String getTarget() {
    return target;
  }

  public GmlEdge withTarget(String target) {
    this.target = target;
    return this;
  }

  public GmlEdge withId(String id) {
    this.id = id;
    return this;
  }

  public List<Data> getDataList() {
    return dataList;
  }

  public void setDataList(List<Data> dataList) {
    this.dataList = dataList;
  }

  public GmlEdge addData(Data data) {
    dataList.add(data);
    return this;
  }

  public JAXBElement<GmlEdge> asJaxbElement() {
    return new JAXBElement<GmlEdge>(QNAME, GmlEdge.class, this);
  }
}
