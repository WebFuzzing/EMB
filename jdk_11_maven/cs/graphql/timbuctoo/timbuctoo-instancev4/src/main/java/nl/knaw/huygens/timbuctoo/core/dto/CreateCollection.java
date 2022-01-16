package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.CollectionNameHelper;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class CreateCollection {
  private final String unprefixedEntityName;
  private String archetypeName;

  private CreateCollection(String unprefixedEntityName) {
    this.unprefixedEntityName = unprefixedEntityName;
  }

  public static CreateCollection forEntityTypeName(String unprefixedEntityTypeName) {
    return new CreateCollection(unprefixedEntityTypeName);
  }

  public static CreateCollection defaultCollection(String vreName) {
    if (Objects.equals("Admin", vreName)) {
      return forEntityTypeName(CollectionNameHelper.defaultEntityTypeName(vreName));
    } else {
      CreateCollection result =
        forEntityTypeName(CollectionNameHelper.defaultEntityTypeName(vreName).substring(vreName.length()));
      result.setArchetypeName("concepts");
      return result;
    }
  }

  public String getEntityTypeName(Vre vre) {
    return CollectionNameHelper.entityTypeName(unprefixedEntityName, vre);
  }

  public String getCollectionName(Vre vre) {
    return CollectionNameHelper.collectionName(unprefixedEntityName, vre);
  }

  public boolean isUnknownCollection(String vreName) {
    if (Objects.equals("Admin", vreName)) {
      return Objects.equals(unprefixedEntityName, CollectionNameHelper.defaultEntityTypeName(vreName));
    } else {
      return Objects.equals(
        unprefixedEntityName,
        CollectionNameHelper.defaultEntityTypeName(vreName).substring(vreName.length())
      );
    }
  }

  public String getRdfUri(Vre vre) {
    return CollectionNameHelper.rdfUri(unprefixedEntityName, vre);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getArchetypeName() {
    return archetypeName;
  }

  private void setArchetypeName(String archetypeName) {
    this.archetypeName = archetypeName;
  }
}
