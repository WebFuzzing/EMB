package nl.knaw.huygens.timbuctoo.crud;

public class InvalidCollectionException extends Exception {
  public InvalidCollectionException(String collectionName) {
    super(collectionName + " is not a known collection");
  }
}
