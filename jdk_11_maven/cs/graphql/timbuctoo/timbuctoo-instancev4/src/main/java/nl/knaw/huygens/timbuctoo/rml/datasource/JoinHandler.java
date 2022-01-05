package nl.knaw.huygens.timbuctoo.rml.datasource;

import nl.knaw.huygens.timbuctoo.rml.datasource.joinhandlers.HashMapBasedJoinHandler;

import java.util.List;
import java.util.Map;

public interface JoinHandler {

  /**
   * When invoked from getRows() in DataSource, this tells the referencing row (identified by outputFieldName from
   * willBeJoinedOn) to map the uri of this row.
   * For sample implementation see: {@link HashMapBasedJoinHandler#resolveReferences}
   *
   * @param valueMap the valueMap for the current row from DataSource
   */
  Map<String, List<String>> resolveReferences(Map<String, String> valueMap);

  /**
   * Every time a referenced triplesMap creates a subject, the RrRefObjectMap will tell it's own datasource to store it
   * via this method.
   * For sample implementation see: {@link HashMapBasedJoinHandler#willBeJoinedOn}
   *
   * @param fieldName the column key from the referencing datasource
   * @param referenceJoinValue the cell value in this datasource
   * @param uri the uri of the referenced object
   * @param outputFieldName the key (UUID) that is generated to look up the uri
   */
  void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName);

}
