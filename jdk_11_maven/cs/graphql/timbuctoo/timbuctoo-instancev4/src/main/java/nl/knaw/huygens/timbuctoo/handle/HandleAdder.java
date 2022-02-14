package nl.knaw.huygens.timbuctoo.handle;

import com.kjetland.dropwizard.activemq.ActiveMQBundle;
import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.PersistentUrlCreator;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionState;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.logging.Logmarkers;
import nl.knaw.huygens.timbuctoo.queued.activemq.ActiveMqQueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueSender;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class HandleAdder implements PersistentUrlCreator {
  public static final String HANDLE_QUEUE = "pids";
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(HandleAdder.class);
  private final PersistenceManager manager;
  private final QueueSender sender;
  private TransactionEnforcer transactionEnforcer;

  public HandleAdder(PersistenceManager manager, ActiveMQBundle activeMqBundle) {
    this.manager = manager;
    this.transactionEnforcer = null;
    ActiveMqQueueCreator<HandleAdderParameters> queueCreator = new ActiveMqQueueCreator<>(
      HandleAdderParameters.class,
      HANDLE_QUEUE,
      activeMqBundle
    );
    queueCreator.registerReceiver(this::actualExecution);
    this.sender = queueCreator.createSender();
  }

  public void init(TransactionEnforcer transactionEnforcer) {
    this.transactionEnforcer = transactionEnforcer;
  }

  private void actualExecution(HandleAdderParameters params) {
    try {
      URI uri = params.getUrlToRedirectTo();
      LOG.info(String.format("Retrieving persistent url for '%s'", uri));
      String persistentId = (manager.persistURL(uri.toString()));
      URI persistentUrl = new URI(manager.getPersistentURL(persistentId));

      transactionEnforcer.execute(timbuctooActions -> {
          try {
            timbuctooActions.addPid(persistentUrl, params.getEntityLookup());
            LOG.info("committed pid");
            return TransactionState.commit();
          } catch (NotFoundException e) {
            LOG.warn("Entity for entityLookup '{}' cannot be found", params.getEntityLookup());
            try {
              manager.deletePersistentId(persistentId);
            } catch (PersistenceException e1) {
              LOG.error("Cannot remove handle with id '{}'", persistentId);
            }
            return TransactionState.rollback();
          }
        }
      );
    } catch (PersistenceException | URISyntaxException e) {
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

  @Override
  public void add(URI uriToRedirectTo, EntityLookup entityLookup) {
    if (transactionEnforcer == null) {
      throw new IllegalStateException("init() must be called before you can add items");
    }
    this.sender.send(new HandleAdderParameters(uriToRedirectTo, entityLookup));
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
}
