package nl.knaw.huygens.timbuctoo.model.vre.vres;

import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;

import java.util.Map;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult.commitAndReturn;

public class DatabaseConfiguredVres implements Vres {

  private final TransactionEnforcer transactionEnforcer;
  private Vres loadedInstance;

  public DatabaseConfiguredVres(TransactionEnforcer transactionEnforcer) {
    this.transactionEnforcer = transactionEnforcer;
  }

  public Optional<Collection> getCollection(String collection) {
    return getLoadedInstance().getCollection(collection);
  }

  public Optional<Collection> getCollectionForType(String type) {
    return getLoadedInstance().getCollectionForType(type);
  }

  public Vre getVre(String vre) {
    return getLoadedInstance().getVre(vre);
  }

  public Map<String, Vre> getVres() {
    return getLoadedInstance().getVres();
  }

  private Vres getLoadedInstance() {
    if (loadedInstance != null) {
      return loadedInstance;
    }
    reload();
    return loadedInstance;
  }

  public void reload() {
    loadedInstance = transactionEnforcer.executeAndReturn(
      timbuctooActions -> commitAndReturn(timbuctooActions.loadVres())
    );
  }
}
