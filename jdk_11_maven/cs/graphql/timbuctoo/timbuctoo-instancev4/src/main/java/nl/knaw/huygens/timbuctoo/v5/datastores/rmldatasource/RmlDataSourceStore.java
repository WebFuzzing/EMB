package nl.knaw.huygens.timbuctoo.v5.datastores.rmldatasource;

import nl.knaw.huygens.timbuctoo.v5.dataset.OptimizedPatchListener;

import java.util.stream.Stream;

public interface RmlDataSourceStore extends OptimizedPatchListener {
  Stream<String> get(String collectionUri);

  void close();

  boolean isClean();
}
