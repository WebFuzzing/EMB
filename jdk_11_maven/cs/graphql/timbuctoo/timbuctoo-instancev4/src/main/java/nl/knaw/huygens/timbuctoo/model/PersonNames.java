package nl.knaw.huygens.timbuctoo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

@JsonIgnoreProperties(value = { "nameUris" })
public class PersonNames {
  public List<PersonName> list;

  public PersonNames() {
    list = Lists.newArrayList();
  }

  public PersonName defaultName() {
    return (list != null && !list.isEmpty()) ? list.get(0) : new PersonName();
  }

  @Override
  public boolean equals(Object obj) {
    return reflectionEquals(this, obj, false);
  }

  @Override
  public int hashCode() {
    return reflectionHashCode(this, false);
  }
}
