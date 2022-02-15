package nl.knaw.huygens.timbuctoo.model;

/*
 * #%L
 * Timbuctoo core
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * A component of a person name.
 * The names used are TEI-element names.
 */
public class PersonNameComponent {

  private Type type;
  private String value;

  public PersonNameComponent() {}

  public PersonNameComponent(Type type, String value) {
    setType(type);
    setValue(value);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = StringUtils.stripToEmpty(value);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof PersonNameComponent) {
      PersonNameComponent that = (PersonNameComponent) object;
      return Objects.equal(this.type, that.type) && Objects.equal(this.value, that.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, value);
  }

  // -------------------------------------------------------------------

  public enum Type {
    SURNAME("surname"), //
    FORENAME("forename"), //
    ROLE_NAME("roleName"), //
    ADD_NAME("addName"), //
    NAME_LINK("nameLink"), //
    GEN_NAME("genName");

    private static final Map<String, Type> MAP = createMap();

    private static Map<String, Type> createMap() {
      Map<String, Type> map = Maps.newHashMap();
      for (Type type : Type.values()) {
        map.put(type.getName(), type);
      }
      return map;
    }

    public static Type getInstance(String name) {
      return MAP.get(name);
    }

    private final String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  }

  @Override
  public String toString() {
    return "PersonNameComponent{" +
      "type=" + type +
      ", value='" + value + '\'' +
      '}';
  }
}
