package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectionalRelationType {

  public static final Logger LOG = LoggerFactory.getLogger(DirectionalRelationType.class);
  private final boolean inverse;
  private final String timId;
  private final String inverseName;

  private String regularName;
  private String dbName;
  private String sourceTypeName;
  private String targetTypeName;

  private boolean reflexive;
  private boolean symmetric;
  private boolean derived;

  public DirectionalRelationType(String regularName, String inverseName, String sourceType, String targetType,
                                 boolean reflexive, boolean symmetric, boolean derived, boolean inverse,
                                 String timId) {
    this.timId = timId;
    this.inverse = inverse;
    this.inverseName = inverseName;
    if (inverse) {
      this.regularName = inverseName;
      this.sourceTypeName = targetType;
      this.targetTypeName = sourceType;
      dbName = regularName;
    } else {
      this.regularName = regularName;
      this.sourceTypeName = sourceType;
      this.targetTypeName = targetType;
      dbName = this.regularName;
    }
    this.reflexive = reflexive;
    this.symmetric = symmetric;
    this.derived = derived;
  }

  public boolean isValid(Collection sourceCollection, Collection targetCollection) {
    return (this.sourceTypeName.equals("") || this.sourceTypeName.equals(sourceCollection.getAbstractType())) &&
      (this.targetTypeName.equals("") || this.targetTypeName.equals(targetCollection.getAbstractType()));
  }

  public String getName() {
    return regularName;
  }

  public String getDbName() {
    return dbName;
  }

  public String getTimId() {
    return timId;
  }
}
