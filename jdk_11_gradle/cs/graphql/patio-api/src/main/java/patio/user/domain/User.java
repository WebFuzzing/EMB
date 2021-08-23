/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.user.domain;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.codec.digest.DigestUtils;
import patio.common.domain.utils.Builder;
import patio.group.domain.UserGroup;

/**
 * Represents the users of patio
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "users")
public final class User {

  @Id @GeneratedValue private UUID id;
  private String name;
  private String email;

  private String password;
  private String otp;

  @Column(name = "otp_creation_date")
  private OffsetDateTime otpCreationDateTime;

  @OneToMany(mappedBy = "user")
  private Set<UserGroup> groups;

  /**
   * Creates a builder to create instances of type {@link User}
   *
   * @return a builder to create instances of type {@link User}
   * @since 0.1.0
   */
  public static Builder<User> builder() {
    return Builder.build(User::new);
  }

  /**
   * Gets id.
   *
   * @return Value of id.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets new id.
   *
   * @param id New value of id.
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Gets name.
   *
   * @return Value of name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets new name.
   *
   * @param name New value of name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets password.
   *
   * @return Value of password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets new password.
   *
   * @param password New value of password.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets email.
   *
   * @return Value of email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets new email.
   *
   * @param email New value of email.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets otp.
   *
   * @return Value of otp.
   */
  public String getOtp() {
    return otp;
  }

  /**
   * Sets new otp.
   *
   * @param otp New value of otp.
   */
  public void setOtp(String otp) {
    this.otp = otp;
  }

  /**
   * Gets the time when the otp was created.
   *
   * @return Otp creation time.
   */
  public OffsetDateTime getOtpCreationDateTime() {
    return otpCreationDateTime;
  }

  /**
   * Sets the {@link OffsetDateTime} when the otp is created.
   *
   * @param otpCreationDateTime Otp creation time.
   */
  public void setOtpCreationDateTime(OffsetDateTime otpCreationDateTime) {
    this.otpCreationDateTime = otpCreationDateTime;
  }

  /**
   * Gets user's groups
   *
   * @return set of UserGroups
   */
  public Set<UserGroup> getGroups() {
    return groups;
  }

  /**
   * Sets user's groups
   *
   * @param groups set of UserGroups the user belongs to
   */
  public void setGroups(Set<UserGroup> groups) {
    this.groups = groups;
  }

  /**
   * Generates a user's md5 hash which can be used for third party services such as Gravatar.
   *
   * @return gets a md5 hash from the user's email
   * @since 0.1.0
   */
  public String getHash() {
    return Optional.ofNullable(this.email)
        .map(String::trim)
        .map(String::toLowerCase)
        .map(DigestUtils::md5Hex)
        .orElse("");
  }
}
