package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExplicitField {
  @JsonProperty("uri")
  private String uri;
  @JsonProperty("isList")
  private boolean isList;
  @JsonProperty("values")
  private Set<String> values;
  @JsonProperty("references")
  private Set<String> references;

  @JsonCreator
  public ExplicitField(@JsonProperty("uri") String uri,
                       @JsonProperty("isList") boolean isList,
                       @JsonProperty("values") Set<String> values,
                       @JsonProperty("references") Set<String> references) {
    this.uri = uri;
    this.isList = isList;
    this.values = values;
    this.references = references;
  }

  public Set<String> getValues() {
    return values;
  }

  public void setValues(Set<String> values) {
    this.values = values;
  }

  public Set<String> getReferences() {
    return references;
  }

  public void setReferences(Set<String> references) {
    this.references = references;
  }

  public String getUri() {
    return uri;
  }

  public Predicate convertToPredicate() {

    Map<String, Long> valueTypes = new HashMap<>();
    Map<String, Long> referenceTypes = new HashMap<>();

    if (values != null) {
      for (String value : values) {
        valueTypes.put(value, 0L);
      }
    }


    if (references != null) {
      for (String reference : references) {
        referenceTypes.put(reference, 0L);
      }
    }

    Predicate convertedPredicate = new Predicate(uri, Direction.OUT);
    convertedPredicate.setIsExplicit(true);
    convertedPredicate.setIsList(isList);
    convertedPredicate.setValueTypes(valueTypes);
    convertedPredicate.setReferenceTypes(referenceTypes);


    return convertedPredicate;
  }

  @JsonProperty("isList")
  public boolean isList() {
    return isList;
  }

  public void setList(boolean list) {
    isList = list;
  }

  public ExplicitField mergeWith(ExplicitField explicitField) {
    ExplicitField mergedExplicitField = new ExplicitField(this.getUri(), this.isList(),
      null, null);

    if (!this.getUri().equals(explicitField.getUri())) {
      throw new IllegalArgumentException("Explicit field URIs do not match.");
    }

    if (this.isList() || explicitField.isList()) {
      mergedExplicitField.setList(true);
    } else {
      mergedExplicitField.setList(false);
    }

    Set<String> values = this.getValues();

    if (explicitField.getValues() != null) {
      for (String value : explicitField.getValues()) {
        if (!values.contains(value)) {
          values.add(value);
        }
      }
    }

    mergedExplicitField.setValues(values);

    Set<String> references = this.getReferences();

    if (explicitField.getReferences() != null) {
      for (String reference : explicitField.getReferences()) {
        if (!references.contains(reference)) {
          references.add(reference);
        }
      }
    }

    mergedExplicitField.setReferences(references);

    return mergedExplicitField;
  }
}

