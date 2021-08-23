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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.user.services.UserService;

/**
 * Business logic regarding {@link User} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultUserService implements UserService {

  private final transient UserRepository userRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param userRepository an instance of {@link UserRepository}
   * @since 0.1.0
   */
  public DefaultUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Iterable<User> listUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> getUser(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public Iterable<User> listUsersByIds(List<UUID> ids) {
    Comparator<User> comparator = Comparator.comparing((User user) -> ids.indexOf(user.getId()));

    return userRepository.findAllByIdInList(ids).stream()
        .sorted(comparator)
        .collect(Collectors.toList());
  }
}
