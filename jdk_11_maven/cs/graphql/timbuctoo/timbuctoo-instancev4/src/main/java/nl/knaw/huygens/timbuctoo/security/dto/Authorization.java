package nl.knaw.huygens.timbuctoo.security.dto;

import java.util.List;

public interface Authorization {
  List<String> getRoles();

  boolean isAllowedToWrite();

  boolean hasAdminAccess();
}
