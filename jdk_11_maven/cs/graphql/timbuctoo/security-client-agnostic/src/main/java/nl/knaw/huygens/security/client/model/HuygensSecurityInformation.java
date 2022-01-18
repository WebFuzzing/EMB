package nl.knaw.huygens.security.client.model;

/*
 * #%L
 * Security Client
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import java.security.Principal;
import java.util.EnumSet;

import com.google.common.base.MoreObjects;
import nl.knaw.huygens.security.core.model.Affiliation;
import nl.knaw.huygens.security.core.model.HuygensPrincipal;

import com.google.common.base.Objects;

public class HuygensSecurityInformation implements SecurityInformation {
  private String commonName;
  private String displayName;
  private String givenName;
  private String surname;
  private String emailAddress;
  private EnumSet<Affiliation> affiliations = EnumSet.noneOf(Affiliation.class);
  private String organization;
  private String persistentID;
  private Principal principal;

  public HuygensSecurityInformation() {

  }

  public HuygensSecurityInformation(HuygensPrincipal huygensPrincipal) {
    setAffiliations(huygensPrincipal.getAffiliations());
    setCommonName(huygensPrincipal.getCommonName());
    setDisplayName(huygensPrincipal.getDisplayName());
    setEmailAddress(huygensPrincipal.getEmailAddress());
    setGivenName(huygensPrincipal.getGivenName());
    setOrganization(huygensPrincipal.getOrganization());
    setPersistentID(huygensPrincipal.getPersistentID());
    setPrincipal(huygensPrincipal);
    setSurname(huygensPrincipal.getSurname());
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public Principal getPrincipal() {
    return principal;
  }

  public void setPrincipal(Principal principal) {
    this.principal = principal;
  }

  @Override
  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  @Override
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  @Override
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public EnumSet<Affiliation> getAffiliations() {
    return affiliations;
  }

  public void setAffiliations(EnumSet<Affiliation> affiliations) {
    this.affiliations = affiliations;
  }

  @Override
  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  @Override
  public String getPersistentID() {
    return persistentID;
  }

  public void setPersistentID(String persistentID) {
    this.persistentID = persistentID;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HuygensSecurityInformation)) {
      return false;
    }

    HuygensSecurityInformation other = (HuygensSecurityInformation) obj;

    boolean isEqual = Objects.equal(other.affiliations, affiliations);
    isEqual &= Objects.equal(other.commonName, commonName);
    isEqual &= Objects.equal(other.displayName, displayName);
    isEqual &= Objects.equal(other.emailAddress, emailAddress);
    isEqual &= Objects.equal(other.givenName, givenName);
    isEqual &= Objects.equal(other.organization, organization);
    isEqual &= Objects.equal(other.persistentID, persistentID);
    isEqual &= Objects.equal(other.surname, surname);
    isEqual &= Objects.equal(other.principal, principal);

    return isEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(affiliations, commonName, displayName, emailAddress, givenName, organization, persistentID, surname, principal);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)//
        .add("persistentID", persistentID)//
        .add("commonName", commonName)//
        .add("displayName", displayName)//
        .add("givenName", givenName)//
        .add("surname", surname)//
        .add("emailAddress", emailAddress)//
        .add("affiliations", affiliations)//
        .add("organization", organization)//
        .add("principal", principal)//
        .toString();
  }

}
