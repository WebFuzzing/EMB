package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.util.Optional;

public class RootData {
  public Optional<User> getCurrentUser() {
    return currentUser;
  }

  private final Optional<User> currentUser;

  public RootData(Optional<User> currentUser) {
    this.currentUser = currentUser;
  }
}
