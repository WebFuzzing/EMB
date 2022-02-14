package nl.knaw.huygens.timbuctoo.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Logmarkers {
  public static Marker databaseInvariant = MarkerFactory.getMarker("Database_invariant_not_upheld");
  public static Marker serviceUnavailable = MarkerFactory.getMarker("Service_unavailable");
  public static Marker configurationFailure = MarkerFactory.getMarker("Configuration_failure");
  public static Marker migration = MarkerFactory.getMarker("Migration");
}
