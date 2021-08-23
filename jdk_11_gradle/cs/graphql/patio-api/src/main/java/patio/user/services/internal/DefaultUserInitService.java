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
package patio.user.services.internal;

import io.micronaut.context.annotation.Value;
import io.micronaut.retry.annotation.Retryable;
import io.micronaut.scheduling.annotation.Scheduled;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Class creating a default user if configuration has enabled this feature and if the user wasn't
 * already created.
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultUserInitService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultUserInitService.class);

  private final transient boolean loadUser;
  private final transient String name;
  private final transient String email;
  private final transient String password;

  private final transient UserRepository userRepository;

  /**
   * Loads user properties and an instance of {@link UserRepository}
   *
   * @param loadUser whether to load default user or not
   * @param name user's name
   * @param email user's email
   * @param password plain text password
   * @param userRepository repository to persist the user
   * @since 0.1.0
   */
  public DefaultUserInitService(
      @Value("${duser.enabled}") boolean loadUser,
      @Value("${duser.name}") String name,
      @Value("${duser.email}") String email,
      @Value("${duser.password}") String password,
      UserRepository userRepository) {
    this.loadUser = loadUser;
    this.name = name;
    this.email = email;
    this.password = password;
    this.userRepository = userRepository;
  }

  /**
   * Task loading the default user if the feature is enabled
   *
   * @since 0.1.0
   */
  @Retryable
  @Scheduled(initialDelay = "10s")
  public void loadDefaultUser() {
    if (loadUser) {
      LOG.info(String.format("loading default user %s", this.email));

      String hashed = BCrypt.hashpw(this.password, BCrypt.gensalt());
      User user =
          User.builder()
              .with(u -> u.setName(this.name))
              .with(u -> u.setEmail(this.email))
              .with(u -> u.setPassword(hashed))
              .build();

      userRepository
          .findByEmailOrCreate(user)
          .ifPresent((u) -> LOG.info(String.format("default user with id %s", u.getId())));
    }
  }
}
