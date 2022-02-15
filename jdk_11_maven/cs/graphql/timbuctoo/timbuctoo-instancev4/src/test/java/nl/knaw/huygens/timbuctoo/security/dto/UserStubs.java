package nl.knaw.huygens.timbuctoo.security.dto;

import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import javax.annotation.Nullable;

public class UserStubs {

  public static User userWithId(final String userId) {
    return new User() {
      @Nullable
      @Override
      public String getDisplayName() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Nullable
      @Override
      public String getPersistentId() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public String getId() {
        return userId;
      }
    };
  }

  public static User userWithPid(final String pid) {
    return new User() {
      @Nullable
      @Override
      public String getDisplayName() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Nullable
      @Override
      public String getPersistentId() {
        return pid;
      }

      @Override
      public String getId() {
        throw new UnsupportedOperationException("Not implemented yet");
      }
    };
  }

  public static User anyUser() {
    return new User() {
      @Nullable
      @Override
      public String getDisplayName() {
        return null;
      }

      @Nullable
      @Override
      public String getPersistentId() {
        return null;
      }

      @Override
      public String getId() {
        return null;
      }
    };
  }
}
