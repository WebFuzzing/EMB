package nl.knaw.huygens.timbuctoo.security.exceptions;

import java.util.UUID;

public class AuthorizationException extends Exception {
  public AuthorizationException(String message) {
    super(message);
  }

  public static AuthorizationException notAllowedToDelete(String collectionName, UUID id) {
    return new AuthorizationException(
      String.format("You are not allowed to delete item with id \"%s\" from \"%s\".", id, collectionName));
  }

  public static AuthorizationException notAllowedToCreate(String collectionName) {
    return new AuthorizationException(String.format("You are not authorized to edit collection %s.", collectionName));
  }

  public static AuthorizationException notAllowedToEdit(String collectionName, UUID id) {
    return new AuthorizationException(
      String.format("You are not allowed to edit item with id \"%s\" from \"%s\".", id, collectionName));
  }
}
