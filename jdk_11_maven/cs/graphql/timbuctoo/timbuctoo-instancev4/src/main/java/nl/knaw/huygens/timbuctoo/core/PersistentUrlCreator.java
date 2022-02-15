package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;

import java.net.URI;

public interface PersistentUrlCreator {
  void add(URI uriToRedirectTo, EntityLookup entityLookup);
}
