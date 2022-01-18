package nl.knaw.huygens.timbuctoo.model.vre.vres;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

class ConfiguredVres implements Vres {
  private final Map<String, Collection> collections = new HashMap<>();
  private Map<String, Vre> vres;

  ConfiguredVres(List<Vre> vres) {
    loadFromVreList(vres);
  }

  private void loadFromVreList(List<Vre> vres) {
    this.vres = vres.stream().collect(toMap(Vre::getVreName, vre1 -> vre1));
    vres.stream()
        .flatMap(vre -> vre.getCollections().values().stream())
        .forEach(collection -> {
          if (collections.containsKey(collection.getCollectionName())) {
            throw new RuntimeException("Collection was defined multiple times: " + collection.getCollectionName());
          }
          collections.put(collection.getCollectionName(), collection);
        });
  }

  public Optional<Collection> getCollection(String collection) {
    return Optional.ofNullable(collections.get(collection));
  }

  public Optional<Collection> getCollectionForType(String type) {
    return collections.values().stream().filter(coll -> Objects.equals(coll.getEntityTypeName(), type)).findAny();
  }

  public Vre getVre(String vre) {
    return vres.get(vre);
  }

  public Map<String, Vre> getVres() {
    return vres;
  }


  @Override
  public void reload() {

  }

}
