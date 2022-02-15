package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ExplicitType {

  private String collectionId;
  @JsonProperty("fields")
  private List<ExplicitField> fields;

  @JsonCreator
  public ExplicitType(@JsonProperty("collectionId") String collectionId,
                      @JsonProperty("fields") List<ExplicitField> fields) {
    this.collectionId = collectionId;
    this.fields = fields;
  }

  public String getCollectionId() {
    return this.collectionId;
  }

  public List<ExplicitField> getFields() {
    return this.fields;
  }

  public Type convertToType() {
    if (this.collectionId.endsWith("List")) {
      throw new RuntimeException("Collection_id cannot end with 'List'");
    }

    Type convertedType = new Type(collectionId);
    Collection<Predicate> predicates = new HashSet<>();

    if (fields == null || fields.isEmpty()) {
      return convertedType;
    }

    for (ExplicitField field : fields) {
      Predicate convertedPredicate = field.convertToPredicate();
      convertedPredicate.setIsExplicit(true);
      predicates.add(convertedPredicate);
    }
    convertedType.setPredicates(predicates);
    return convertedType;
  }

}
