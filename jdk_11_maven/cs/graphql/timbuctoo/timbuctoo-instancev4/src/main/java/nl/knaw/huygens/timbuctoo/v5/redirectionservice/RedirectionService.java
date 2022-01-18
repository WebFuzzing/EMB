package nl.knaw.huygens.timbuctoo.v5.redirectionservice;

import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.logging.Logmarkers;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueSender;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.exceptions.RedirectionServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class RedirectionService {
  private static final Logger LOG = LoggerFactory.getLogger(RedirectionService.class);
  private final QueueSender sender;
  private final QueueSender oldSender;
  protected TransactionEnforcer transactionEnforcer;

  public RedirectionService(String queueName, QueueManager queueManager) {
    QueueCreator<RedirectionServiceParameters> queueCreator =
      queueManager.createQueue(RedirectionServiceParameters.class, queueName);
    QueueCreator<RedirectionServiceParameters> oldQueueCreator =
      queueManager.createQueue(RedirectionServiceParameters.class, queueName + "Old");

    queueCreator.registerReceiver(this::actualExecution);
    this.sender = queueCreator.createSender();

    oldQueueCreator.registerReceiver(this::oldActualExecution);
    this.oldSender = oldQueueCreator.createSender();
  }

  private void oldActualExecution(RedirectionServiceParameters params) {
    try {
      oldSavePid(params);
    } catch (PersistenceException | URISyntaxException e) {
      LOG.error(Logmarkers.serviceUnavailable, "Could not create handle", e);
      if (params.getRetries() < 5) {
        LOG.warn(String.format("Re-adding %s%s job to the queue for '%s' '%s'",
          params.getRetries() + 1, getOrdinalSuffix(params.getRetries() + 1),
          params.getEntityLookup(),
          params.getUrlToRedirectTo()
        ));
        this.oldSender.send(params.nextTry());
      }
    }
  }

  private void actualExecution(RedirectionServiceParameters params) {
    try {
      savePid(params);
    } catch (PersistenceException | URISyntaxException | RedirectionServiceException e) {
      LOG.error(Logmarkers.serviceUnavailable, "Could not create handle", e);
      if (params.getRetries() < 5) {
        LOG.warn(String.format("Re-adding %s%s job to the queue for '%s' '%s'",
          params.getRetries() + 1, getOrdinalSuffix(params.getRetries() + 1),
          params.getEntityLookup(),
          params.getUrlToRedirectTo()
        ));
        this.sender.send(params.nextTry());
      }
    }
  }

  // gogo gadgetstackoverflow
  private String getOrdinalSuffix(int value) {
    int hunRem = value % 100;
    int tenRem = value % 10;

    if (hunRem - tenRem == 10) {
      return "th";
    }
    switch (tenRem) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      default:
        return "th";
    }
  }

  protected abstract void oldSavePid(RedirectionServiceParameters params)
    throws PersistenceException, URISyntaxException;

  protected abstract void savePid(RedirectionServiceParameters params) throws
    PersistenceException, URISyntaxException, RedirectionServiceException;

  public final void oldAdd(URI uriToRedirectTo, EntityLookup entityLookup) {
    if (transactionEnforcer == null) {
      throw new IllegalStateException("init() must be called before you can add items");
    }
    this.oldSender.send(new RedirectionServiceParameters(uriToRedirectTo, entityLookup));
  }

  public final void add(URI uriToRedirectTo, EntityLookup entityLookup) {
    this.sender.send(new RedirectionServiceParameters(uriToRedirectTo, entityLookup));
  }

  public final void init(TransactionEnforcer transactionEnforcer) {
    this.transactionEnforcer = transactionEnforcer;
  }
}
