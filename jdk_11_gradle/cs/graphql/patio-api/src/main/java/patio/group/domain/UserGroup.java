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
package patio.group.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import patio.common.domain.utils.Builder;
import patio.user.domain.User;

/**
 * Represents the relation between users and groups
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "users_groups")
public final class UserGroup {

  @EmbeddedId private UserGroupKey id;

  @ManyToOne
  @MapsId("user_id")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @MapsId("group_id")
  @JoinColumn(name = "group_id")
  private Group group;

  @Column(name = "is_admin")
  private boolean admin;

  /**
   * Creates a new {@link UserGroup} from an {@link User} and a {@link Group}
   *
   * @param user an instance of {@link User}
   * @param group an instance of {@link Group}
   */
  public UserGroup(User user, Group group) {
    this.user = user;
    this.group = group;
    this.id = new UserGroupKey(user.getId(), group.getId());
  }

  /** Default constructor */
  public UserGroup() {
    /* empty */
  }

  /**
   * Creates a new {@link Builder} to create an instance of type {@link UserGroup}
   *
   * @return an instance of {@link Builder} to create instances of type {@link UserGroup}
   * @since 0.1.0
   */
  public static Builder<UserGroup> builder() {
    return Builder.build(UserGroup::new);
  }

  /**
   * Returns the current user
   *
   * @return an instance of {@link User}
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets up the current {@link User}
   *
   * @param user the current user
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Returns the current group
   *
   * @return an instance of {@link Group}
   */
  public Group getGroup() {
    return group;
  }

  /**
   * Sets the current group
   *
   * @param group the group to set
   */
  public void setGroup(Group group) {
    this.group = group;
  }

  /**
   * Gets admin value.
   *
   * @return Value of admin.
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets admin value.
   *
   * @param admin New value of admin.
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  /**
   * Returns the user group id
   *
   * @return the {@link UserGroup} id
   */
  public UserGroupKey getId() {
    return id;
  }

  /**
   * Sets user group's id
   *
   * @param id sets {@link UserGroup} id
   */
  public void setId(UserGroupKey id) {
    this.id = id;
  }
}
