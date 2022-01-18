package nl.knaw.huygens.timbuctoo.core.dto;

import java.util.List;
import java.util.function.Function;

/**
 * A wrapper for retrieving more than one entity from a data source.
 * This wrapper should make it possible to close the transaction, when needed.
 */
public interface DataStream<T> {
  <U> List<U> map(Function<T, U> mapping);
}
