package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

/**
 * An importer imports a dataset, consisting of multiple collections.
 *
 * <pre>
 *           +-------------+                    +---------------+
 *           |             |                    |               |
 *           | waiting for |   startCollection  | Waiting for   +--+
 *           | collections | +----------------> | propertynames |  |
 *           |             |                    |               |  |
 *           +-------------+                    +--------^------+  | registerPropertyName
 *                 ^                                 |   |         |
 *                 |                     startEntity |   +---------+
 *                 |                          +------v-------+
 *                 |                          |              +---------------+
 *                 |                          | waiting for  +--+            |
 *                 +--------------------------+ values       |  |            |
 *             finishCollection               |              |  |            |
 *                                            +----^-----^---+  | setValue   |
 *                                                 |     |      |            |
 *                                                 |     +------+            |
 *                                                 |                         |
 *                                                 |                         |
 *                                                 |           finishEntity  |
 *                                                 +-------------------------+
 *
 * </pre>
 */
public class Importer {

  private final StateMachine<?> importer;
  private final ResultReporter resultReporter;

  public Importer(StateMachine<?> importer, ResultReporter resultReporter) {
    this.importer = importer;
    this.resultReporter = resultReporter;
  }

  /**
   * Start a new collection.
   *
   * @param collectionName Name of the collection.
   */
  public void startCollection(String collectionName) {
    resultReporter.startCollection(collectionName, importer.startCollection(collectionName));
  }

  /**
   * Register the given name for the column with the given id.
   * Assumes that we are inside a collection, before the first entity.
   */
  public void registerPropertyName(int columnId, String name) {
    resultReporter.registerPropertyName(columnId, name, importer.registerPropertyName(columnId, name));
  }

  /**
   * Start a new entity.
   * Assumes we are inside a collection (between startCollection and finishCollection).
   * Add property values with {@link #setValue(int, String)}. Close with {@link #finishEntity()}.
   */
  public void startEntity() {
    resultReporter.startEntity();
    importer.startEntity();
  }

  /**
   * Sets a property for the current entity to the given value.
   *
   * @param id    Identifier of the property, previously registered with {@link #registerPropertyName(int, String)}.
   * @param value Property value.
   */
  public void setValue(int id, String value) {
    resultReporter.setValue(id, value, importer.setValue(id, value));
  }

  /**
   * Close the current entity.
   */
  public void finishEntity() {
    resultReporter.finishEntity(importer.finishEntity());
  }

  /**
   * Close the current collection.
   */
  public void finishCollection() {
    resultReporter.finishCollection();
    importer.finishCollection();
  }

}
