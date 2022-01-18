package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import javax.ws.rs.core.UriBuilder;

public class UserUriCreator {
  private final UriHelper uriHelper;

  public UserUriCreator(UriHelper uriHelper) {

    this.uriHelper = uriHelper;
  }

  public String create(User user) {
    return UriBuilder.fromUri(uriHelper.getBaseUri()).path("users").path(user.getPersistentId()).build().toString();
  }
}
