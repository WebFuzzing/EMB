package nl.knaw.huygens.timbuctoo.model;

/*
 * #%L
 * Timbuctoo model
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class AltName {
  private String nametype;
  private String name;

  public AltName(String nametype, String name) {

    this.nametype = nametype;
    this.name = name;
  }

  public AltName() {
  }

  public String getNametype() {
    return nametype;
  }

  public void setNametype(String nametype) {
    this.nametype = nametype;
  }

  public String getDisplayName() {
    return name;
  }

  public void setDisplayName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
