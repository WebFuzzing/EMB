package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.core.dto.RelationType;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperations;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;

class TinkerPopOperationsForMigrations {
  static Vres getVres(TinkerPopGraphManager graphManager) {
    //we're only going to call one method that will not call most of the dependencies that TinkerPopOperations needs
    try (TinkerPopOperations dataStoreOperations = new TinkerPopOperations(graphManager)) {
      return dataStoreOperations.loadVres();
    }
  }

  static void saveRelationTypes(TinkerPopGraphManager graphManager, RelationType... relationTypes) {
    try (TinkerPopOperations dataStoreOperations = new TinkerPopOperations(graphManager)) {
      dataStoreOperations.saveRelationTypes(relationTypes);
      dataStoreOperations.success();
    }
  }

  static TinkerPopOperations forInitDb(TinkerPopGraphManager graphManager) {
    return new TinkerPopOperations(graphManager);
  }
}
