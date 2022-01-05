package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.slf4j.Logger;

import java.net.URI;
import java.time.Clock;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public class EntityFinisherHelper {
  private static final Logger LOG = getLogger(EntityFinisherHelper.class);
  private final Change change;
  private final UrlGenerator urlGenerator;

  public EntityFinisherHelper() {
    this((collection, id, rev) -> URI.create("http://example.org"), Clock.systemDefaultZone(), "rdf-importer");
  }

  public EntityFinisherHelper(UrlGenerator urlGenerator, Clock clock, String userId) {
    this.urlGenerator = urlGenerator;
    change = new Change();
    change.setTimeStamp(clock.instant().toEpochMilli());
    change.setUserId(userId);
  }

  public UUID newId() {
    return UUID.randomUUID();
  }

  public int getRev() {
    return 1;
  }

  public Change getChangeTime() {
    return change;
  }

  public URI getRdfUri(String collection, UUID id) {
    return urlGenerator.apply(collection, id, null);
  }
}
