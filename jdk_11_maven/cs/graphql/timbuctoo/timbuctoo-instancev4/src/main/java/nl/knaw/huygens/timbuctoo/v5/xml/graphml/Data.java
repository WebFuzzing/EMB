package nl.knaw.huygens.timbuctoo.v5.xml.graphml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
public class Data {

  public static final String LOCAL_NAME = "data";
  public static final QName QNAME = new QName(Gml.NAMESPACE, LOCAL_NAME);

  @XmlAttribute private String key;
  @XmlValue private String value;

  public String getKey() {
    return key;
  }

  public Data withKey(String key) {
    this.key = key;
    return this;
  }

  public String getValue() {
    return value;
  }

  public Data withValue(String value) {
    this.value = value;
    return this;
  }

  public JAXBElement<Data> asJaxbElement() {
    return new JAXBElement<Data>(QNAME, Data.class, this);
  }
}
