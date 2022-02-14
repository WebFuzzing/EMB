package nl.knaw.huygens.timbuctoo.v5.dropwizard.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseAvailabilityCheck extends HealthCheck {
  public static final Logger LOG = LoggerFactory.getLogger(DatabaseAvailabilityCheck.class);
  private final DataSetRepository dataSetRepository;

  public DatabaseAvailabilityCheck(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  protected Result check() throws Exception {
    StringBuilder message = new StringBuilder();
    for (DataSet dataSet : dataSetRepository.getDataSets()) {
      LOG.debug("checking unavailable stores for '{}'", dataSet.getMetadata().getCombinedId());
      for (String unavailableStore : dataSet.getUnavailableStores()) {
        message.append(String.format(
            "Store '%s' of data set '%s' is unavailable.\n ",
            unavailableStore,
            dataSet.getMetadata().getCombinedId()
        ));
      }
    }
    if (message.length() > 0) {
      return Result.unhealthy(message.toString());
    }

    return Result.healthy();
  }
}
