package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;

import java.io.IOException;

public interface DatabaseMigration {
  void execute(TinkerPopGraphManager graphWrapper) throws IOException;
}
