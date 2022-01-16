package nl.knaw.huygens.timbuctoo.core.dto.dataset;

import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class VresDto implements Vres {

  private Map<String, Collection> collectionsByName;
  private Map<String, Collection> collectionsByType;

  @Override
  public void reload() {
  }

  private void init() {
    if (this.collectionsByName == null) {
      collectionsByName = new HashMap<>();
      collectionsByType = new HashMap<>();
      getVres().values().stream()
          .flatMap(vre -> vre.getCollections().values().stream())
          .forEach(collection -> {
            collectionsByName.put(collection.getCollectionName(), collection);
            collectionsByType.put(collection.getEntityTypeName(), collection);
          });
    }
  }

  @Override
  public Optional<Collection> getCollection(String collection) {
    init();
    return Optional.ofNullable(collectionsByName.get(collection));
  }

  @Override
  public Optional<Collection> getCollectionForType(String type) {
    init();
    return Optional.ofNullable(collectionsByType.get(type));
  }

  @Override
  public Vre getVre(String vre) {
    return getVres().get(vre);
  }
}
