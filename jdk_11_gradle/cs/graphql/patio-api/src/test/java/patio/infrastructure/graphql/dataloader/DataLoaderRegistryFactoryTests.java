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
package patio.infrastructure.graphql.dataloader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.user.graphql.UserBatchLoader;

/**
 * Tests {@link DataLoaderRegistryFactory}
 *
 * @since 0.1.0
 */
public class DataLoaderRegistryFactoryTests {

  @Test
  void testInitialization() {
    // given: an instance of factory
    DataLoaderRegistryFactory factory = new DataLoaderRegistryFactory();

    // when: adding required data loaders
    UserBatchLoader mockedLoader = Mockito.mock(UserBatchLoader.class);
    DataLoaderRegistry registry = factory.create(mockedLoader);

    // then: you should be able to retrieve data loaders by its key
    assertNotNull(registry.getDataLoader(DataLoaderRegistryFactory.DL_USERS_BY_IDS));
  }
}
