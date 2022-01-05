package nl.knaw.huygens.timbuctoo.rml.datasource.joinhandlers;

import nl.knaw.huygens.timbuctoo.rml.datasource.JoinHandler;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapBasedJoinHandler implements JoinHandler {
  private Map<String, Tuple<String, Map<String, List<String>>>> cachedUris = new HashMap<>();


  /**
   * When invoked from getRows() in DataSource, this adds the unique key (UUID) of the referencing row/subject in other
   * data source as key and the uri of this row as the value the mapped values for this row.
   *
   * <p>
   * Given this entry in cachedUris: {
   *   "825d6cb3-0d8f-416f-b4ff-73e525e1d9b4": Tuple[
   *     "dependsOnA", {
   *       "a1": [
   *         "http://example.org/a1"
   *       ]
   *     }
   *   ]
   * }
   *
   * And this valueMap: {
   *    "rdfUri": "http://example.org/y2",
   *    "ID": "y2",
   *    "dependsOnA": "a1"
   * }
   *
   * Add reference to for the combination "dependsOn" -> "a1" to valueMap like this: {
   *    "rdfUri": "http://example.org/y2",
   *    "ID": "y2",
   *    "dependsOnA": "a1"
   *    "825d6cb3-0d8f-416f-b4ff-73e525e1d9b4": [  <-- is the unique ID of the referencing row/subject
   *      "http://example.org/a1"
   *    ]
   * }
   * </p>
   *
   * @param valueMap the valueMap for the current row from DataSource
   */
  @Override
  public Map<String, List<String>> resolveReferences(Map<String, String> valueMap) {
    Map<String, List<String>> result = new HashMap<>();
    for (Map.Entry<String, Tuple<String, Map<String, List<String>>>> stringMapEntry : cachedUris.entrySet()) {
      final Tuple<String, Map<String, List<String>>> stringMapTuple = stringMapEntry.getValue();

      List<String> uris = stringMapTuple.getRight().get(valueMap.get(stringMapTuple.getLeft()));

      result.put(stringMapEntry.getKey(), uris);
    }
    return result;
  }

  /**
   * Every time a referenced triplesMap creates a subject, the RrRefObjectMap will tell it's own datasource to store it
   * via this method.
   *
   * <p>
   * Given willBeJoinedOn(
   *    fieldName="dependsOnA",
   *    referenceJoinValue="a1",
   *    uri="http://example.org/a1",
   *    outputFieldName="825d6cb3-0d8f-416f-b4ff-73e525e1d9b4"
   * )
   *
   * Make HashMap entry: {
   *   "825d6cb3-0d8f-416f-b4ff-73e525e1d9b4": Tuple[
   *     "dependsOnA", {
   *       "a1": [
   *         "http://example.org/a1"
   *       ]
   *     }
   *   ]
   * }
   * </p>
   *
   * @param fieldName the column key from the referencing datasource. During the join we get a map of
   * @param referenceJoinValue the cell value in this datasource
   * @param uri the subject that was generated for the row that contains the referenceJoinValue
   * @param outputFieldName the key that the referencing ObjectMap will use to look up the uri
   */
  @Override
  public void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName) {
    if (referenceJoinValue != null) {
      cachedUris.computeIfAbsent(outputFieldName, x -> Tuple.tuple(fieldName, new HashMap<>()))
        .getRight()
        .computeIfAbsent(referenceJoinValue, x -> new ArrayList<>())
        .add(uri);
    }
  }
}

