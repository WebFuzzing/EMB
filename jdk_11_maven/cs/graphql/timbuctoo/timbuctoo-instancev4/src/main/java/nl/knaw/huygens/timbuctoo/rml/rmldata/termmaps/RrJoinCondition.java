package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

public class RrJoinCondition {
  private String childField;
  private String parentField;

  public RrJoinCondition(String childField, String parentField) {
    this.childField = childField;
    this.parentField = parentField;
  }

  public String getChildField() {
    return childField;
  }

  public String getParentField() {
    return parentField;
  }

}
