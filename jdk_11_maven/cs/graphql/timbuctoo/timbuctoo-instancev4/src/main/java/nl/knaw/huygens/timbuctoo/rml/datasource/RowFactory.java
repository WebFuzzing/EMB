package nl.knaw.huygens.timbuctoo.rml.datasource;

import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.Row;

import java.util.Map;

public interface RowFactory {
  JoinHandler getJoinHandler();

  Row makeRow(Map<String, String> values, ErrorHandler errorHandler);
}
