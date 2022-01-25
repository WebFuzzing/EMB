package nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto;

import java.util.Set;

/**
 * The class to configure the views for the front-end with.
 */
public class Component {
  private String type;
  private String value;
  private Set<Component> subComponents;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Set<Component> getSubComponents() {
    return subComponents;
  }

  public void setSubComponents(Set<Component> subComponents) {
    this.subComponents = subComponents;
  }
}
