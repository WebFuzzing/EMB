package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;

import java.util.Map;

public interface SchemaUpdater {
  void replaceSchema(Map<String, Type> newSchema) throws SchemaUpdateException;
}
