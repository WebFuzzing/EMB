package nl.knaw.huygens.timbuctoo.bulkupload.savers;

import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;

import java.util.Map;

public interface Saver<T> {
  T addEntity(T collection, Map<String, String> currentProperties);

  T addCollection(String collectionName);

  void addPropertyDescriptions(T collection, ImportPropertyDescriptions importPropertyDescriptions);
}
