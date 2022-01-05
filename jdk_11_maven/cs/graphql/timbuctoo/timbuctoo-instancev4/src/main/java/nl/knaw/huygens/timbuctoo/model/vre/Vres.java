package nl.knaw.huygens.timbuctoo.model.vre;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;

import java.util.Map;
import java.util.Optional;

public interface Vres {

  Optional<Collection> getCollection(String collection);

  Optional<Collection> getCollectionForType(String type);

  Vre getVre(String vre);

  Map<String, Vre> getVres();

  void reload();
}
