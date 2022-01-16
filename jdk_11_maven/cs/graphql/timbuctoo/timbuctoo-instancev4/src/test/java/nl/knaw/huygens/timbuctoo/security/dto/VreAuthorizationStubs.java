package nl.knaw.huygens.timbuctoo.security.dto;

import com.google.common.collect.Lists;

import java.util.List;

public class VreAuthorizationStubs {
  public static VreAuthorization authorizationWithRole(final String role) {
    return new VreAuthorization() {
      @Override
      public String getVreId() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public String getUserId() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public List<String> getRoles() {
        return Lists.newArrayList(role);
      }

      @Override
      public boolean hasAdminAccess() {
        throw new UnsupportedOperationException("Not implemented");
      }
    };
  }
}
