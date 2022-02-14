package nl.knaw.huygens.timbuctoo.databaselog;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;

class ChangeStringComparator implements Comparator<String> {
  private static final Logger LOG = LoggerFactory.getLogger(ChangeStringComparator.class);
  private final ObjectMapper objectMapper;

  public ChangeStringComparator() {
    objectMapper = new ObjectMapper();
  }

  @Override
  public int compare(String o1, String o2) {
    long timeStamp1 = getTimestampFromChangeString(o1);
    long timeStamp2 = getTimestampFromChangeString(o2);
    return Long.compare(timeStamp1, timeStamp2);
  }

  private long getTimestampFromChangeString(String changeString) {
    long timeStamp1;
    try {
      timeStamp1 = objectMapper.readValue(changeString, Change.class).getTimeStamp();
    } catch (IOException e) {
      LOG.error(databaseInvariant, "Cannot convert to change", e);
      LOG.error(databaseInvariant, "Change '{}'", changeString);
      throw new RuntimeException(changeString + " is not a valid.");
    }
    return timeStamp1;
  }

}
