package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class Mutation<T> implements DataFetcher<T> {

  private final Runnable schemaUpdater;

  public Mutation(Runnable schemaUpdater) {

    this.schemaUpdater = schemaUpdater;
  }

  @Override
  public final T get(DataFetchingEnvironment environment) {
    T value = this.executeAction(environment);
    schemaUpdater.run();
    return value;
  }

  protected abstract T executeAction(DataFetchingEnvironment environment);

}
