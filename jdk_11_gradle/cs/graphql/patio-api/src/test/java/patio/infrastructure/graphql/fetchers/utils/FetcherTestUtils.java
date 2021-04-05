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
package patio.infrastructure.graphql.fetchers.utils;

import graphql.schema.DataFetchingEnvironment;
import java.util.HashMap;
import java.util.Map;
import org.dataloader.DataLoader;
import org.mockito.Mockito;
import patio.infrastructure.graphql.Context;
import patio.user.domain.User;

/**
 * Contains functions that may be of some help when dealing with data fetchers
 *
 * @since 0.1.0
 */
public abstract class FetcherTestUtils {

  /**
   * Generates a mocked DataFetchingEnvironment with the user as authenticatedUser and the arguments
   * of the map
   *
   * @param authenticatedUser The authenticatedUser
   * @param arguments The map of arguments of the environment
   * @return an mocked instance of {@link DataFetchingEnvironment}
   * @since 0.1.0
   */
  public static DataFetchingEnvironment generateMockedEnvironment(
      User authenticatedUser, Map<String, Object> arguments) {
    // create a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and a mocked context
    var mockedContext = Mockito.mock(Context.class);

    // mocking context behavior to return the authenticatedUser
    Mockito.when(mockedContext.getAuthenticatedUser()).thenReturn(authenticatedUser);

    // mocking environment behavior to return the context
    Mockito.when(mockedEnvironment.getContext()).thenReturn(mockedContext);

    // mocking environment behavior to return the arguments
    arguments.forEach((k, v) -> Mockito.when(mockedEnvironment.getArgument(k)).thenReturn(v));

    return mockedEnvironment;
  }

  /**
   * Creates an instance of {@link MockedEnvironmentBuilder} to build an instance of {@link
   * DataFetchingEnvironment}
   *
   * @return an instance of {@link MockedEnvironmentBuilder}
   * @since 0.1.0
   */
  public static MockedEnvironmentBuilder create() {
    return new MockedEnvironmentBuilder();
  }

  /**
   * Builds an instance of {@link DataFetchingEnvironment}
   *
   * @since 0.1.0
   */
  public static class MockedEnvironmentBuilder {

    private User authenticatedUser;
    private DataFetchingEnvironment environment = Mockito.mock(DataFetchingEnvironment.class);
    private Object source;
    private Map<String, DataLoader> dataLoaderMap = new HashMap<>();
    private Map<String, Object> arguments = new HashMap<>();

    /**
     * Adds an authenticated user to the GraphQL context
     *
     * @param user instance of {@link User}
     * @return the current builder instance
     * @since 0.1.0
     */
    public MockedEnvironmentBuilder authenticatedUser(User user) {
      this.authenticatedUser = user;
      return this;
    }

    /**
     * Adds a new {@link DataLoader} to the GraphQL environment by a key
     *
     * @param key to be able to locate the data loader afterwards
     * @param dataLoader instance of {@link DataLoader}
     * @return the current builder instance
     * @since 0.1.0
     */
    public MockedEnvironmentBuilder dataLoader(String key, DataLoader dataLoader) {
      this.dataLoaderMap.put(key, dataLoader);
      return this;
    }

    /**
     * Adds a query's argument value
     *
     * @param key to be able to locate the argument afterwards
     * @param value argument's value
     * @return the current builder instance
     * @since 0.1.0
     */
    public MockedEnvironmentBuilder argument(String key, Object value) {
      this.arguments.put(key, value);
      return this;
    }

    /**
     * Adds a fetch environment source
     *
     * @param source instance of the source object
     * @return the current builder instance
     * @since 0.1.0
     */
    public MockedEnvironmentBuilder source(Object source) {
      this.source = source;
      return this;
    }

    /**
     * Returns the {@link DataFetchingEnvironment} instance built
     *
     * @return an instance of {@link DataFetchingEnvironment}
     * @since 0.1.0
     */
    public DataFetchingEnvironment build() {
      // and a mocked context
      var mockedContext = Mockito.mock(Context.class);

      // mocking context behavior to return the authenticatedUser
      Mockito.when(mockedContext.getAuthenticatedUser()).thenReturn(authenticatedUser);

      // mocking environment behavior to return the context
      Mockito.when(environment.getContext()).thenReturn(mockedContext);

      // mocking environment behavior to return the arguments
      arguments.forEach((k, v) -> Mockito.when(environment.getArgument(k)).thenReturn(v));

      // mocking environment source
      Mockito.when(environment.getSource()).thenReturn(this.source);

      // mocking data loaders retrieval
      dataLoaderMap.forEach((k, v) -> Mockito.when(environment.getDataLoader(k)).thenReturn(v));

      return environment;
    }
  }
}
