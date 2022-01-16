package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

public class ImportPropertyDescription {
  private String propertyName;
  private boolean unique;
  private String[] metadata = new String[0];
  private final Integer id;
  private final int order;
  private String type = "basic";

  ImportPropertyDescription(Integer id, int order) {
    this.id = id;
    this.order = order;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public Integer getId() {
    return id;
  }

  public int getOrder() {
    return order;
  }

}
