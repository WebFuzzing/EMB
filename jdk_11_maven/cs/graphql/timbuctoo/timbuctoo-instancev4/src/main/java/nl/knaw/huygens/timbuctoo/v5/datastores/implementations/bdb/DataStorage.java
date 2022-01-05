package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;

public interface DataStorage {
  String getValue();

  void setValue(String newValue) throws DatabaseWriteException;

  void close() throws Exception;

  void commit();

  void beginTransaction();

  boolean isClean();

  void empty();
}
