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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

@JsonIgnoreProperties(Change.CLIENT_PROP_USERNAME)
public class Change {

  public static final String CLIENT_PROP_TIME_STAMP = "timeStamp";
  public static final String CLIENT_PROP_USER_ID = "userId";
  public static final String CLIENT_PROP_VRE_ID = "vreId";
  public static final String CLIENT_PROP_USERNAME = "username";

  /**
   * Returns a new {@code Change} instance for internal use.
   */
  public static Change newInternalInstance() {
    return new Change("timbuctoo", "timbuctoo");
  }

  /**
   * Returns a new time stamp.
   */
  private static long newTimeStamp() {
    return new Date().getTime();
  }

  // -------------------------------------------------------------------

  @JsonProperty(CLIENT_PROP_TIME_STAMP)
  private long timeStamp;
  @JsonProperty(CLIENT_PROP_USER_ID)
  private String userId;
  @JsonProperty(CLIENT_PROP_VRE_ID)
  private String vreId;

  public Change() {
  }

  public Change(long timeStamp, String userId, String vreId) {
    this.timeStamp = timeStamp;
    this.userId = userId;
    this.vreId = vreId;
  }

  public Change(String userId, String vreId) {
    this(newTimeStamp(), userId, vreId);
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getVreId() {
    return vreId;
  }

  public void setVreId(String vreId) {
    this.vreId = vreId;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Change)) {
      return false;
    }

    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
