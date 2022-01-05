package nl.knaw.huygens.timbuctoo.security.dto;

import com.google.common.collect.Lists;

import java.util.List;

/*
 * #%L
 * Timbuctoo services
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

/**
 * A helper class that defines the possible user roles.
 */
public class UserRoles {
  public static final String ADMIN_ROLE = "ADMIN";
  public static final String UNVERIFIED_USER_ROLE = "UNVERIFIED_USER";
  public static final String USER_ROLE = "USER";

  private UserRoles() {
    throw new AssertionError("Non-instantiable class");
  }

  public static List<String> getAll() {
    return Lists.newArrayList(ADMIN_ROLE, USER_ROLE, UNVERIFIED_USER_ROLE);
  }

  public static List<String> getVerified() {
    return Lists.newArrayList(ADMIN_ROLE, USER_ROLE);
  }

}
