package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
public class Key {

  public static final String LOCAL_NAME = "key";
  public static final QName QNAME = new QName(Gml.NAMESPACE, LOCAL_NAME);

  @XmlAttribute() private String id;
  @XmlAttribute(name = "attr.name") private String attrName;
  @XmlAttribute(name = "attr.type") private String attrType;
  @XmlAttribute(name = "for") private String forAttr;

  public Key() {}

  public Key(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public Key withId(String id) {
    this.id = id;
    return this;
  }

  public String getAttrName() {
    return attrName;
  }

  public Key withAttrName(String attrName) {
    this.attrName = attrName;
    return this;
  }

  public String getAttrType() {
    return attrType;
  }

  public Key withAttrType(String attrType) {
    this.attrType = attrType;
    return this;
  }

  public String getFor() {
    return forAttr;
  }

  public Key withFor(String forValue) {
    this.forAttr = forValue;
    return this;
  }

  public JAXBElement<Key> asJaxbElement() {
    return new JAXBElement<Key>(QNAME, Key.class, this);
  }
}
