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
package patio.user.graphql;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import org.dataloader.BatchLoader;
import patio.infrastructure.utils.IterableUtils;
import patio.user.domain.User;
import patio.user.services.UserService;
import patio.user.services.internal.DefaultUserService;

/**
 * Loads a list of {@link User} by their ids
 *
 * @since 0.1.0
 */
@Singleton
public class UserBatchLoader implements BatchLoader<UUID, User> {

  private final transient UserService userService;

  /**
   * Initializes the data loader with a {@link DefaultUserService}
   *
   * @param userService required to retrieve users
   * @since 0.1.0
   */
  public UserBatchLoader(UserService userService) {
    this.userService = userService;
  }

  @Override
  public CompletionStage<List<User>> load(List<UUID> keys) {
    List<User> userList =
        IterableUtils.iterableToStream(userService.listUsersByIds(keys))
            .collect(Collectors.toList());
    return CompletableFuture.supplyAsync(() -> userList);
  }
}
