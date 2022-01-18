package nl.knaw.huygens.timbuctoo.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.GraphReadUtils;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
  private static final Map<String, PropertyDescriptor> propertyDescriptors = new HashMap<>();

  static {
    PropertyParserFactory propertyParserFactory = new PropertyParserFactory();
    PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory(propertyParserFactory);

    PropertyDescriptor personDescriptor = propertyDescriptorFactory.getComposite(
            propertyDescriptorFactory.getLocal("person_names", PersonNames.class),
            propertyDescriptorFactory.getLocal("wwperson_tempName", String.class));

    propertyDescriptors.put("person", personDescriptor);
    propertyDescriptors.put("document", propertyDescriptorFactory.getLocal("document_title", String.class));
  }

  private String label;
  private String type;
  private String key;

  public Node(Vertex vertex, String entityTypeName) throws IOException {
    this.type = getVertexType(vertex);

    this.key = entityTypeName == null ? this.type + "s/" + vertex.property("tim_id").value() :
      entityTypeName + "s/" + vertex.property("tim_id").value();

    if (!propertyDescriptors.containsKey(type)) {
      throw new IOException("type not supported: " + type);
    }

    this.label = propertyDescriptors.get(type).get(vertex);
  }

  public String getLabel() {
    return label;
  }

  public String getType() {
    return type;
  }

  public String getKey() {
    return key;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (other == this) {
      return true;
    }

    if (!(other instanceof Node)) {
      return false;
    }

    Node otherNode = (Node) other;

    return otherNode.getKey().equals(key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return "Node{" +
            "label='" + label + '\'' +
            ", type='" + type + '\'' +
            ", key='" + key + '\'' +
            '}';
  }

  private String getVertexType(Vertex vertex)  {
    String types = (String) vertex.property("types").value();
    ObjectMapper mapper = new ObjectMapper();


    List<String> typeList = null;
    try {
      typeList = mapper.readValue(types,
              mapper.getTypeFactory().constructCollectionType(List.class, String.class));
    } catch (IOException e) {
      typeList = new ArrayList<>();
      typeList.add("");
    }

    typeList.sort((o1, o2) -> o1.length() - o2.length());
    return typeList.get(0);
  }

}
