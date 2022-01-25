package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.logging.Logmarkers;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class MakePidsAbsoluteUrls implements DatabaseMigration {
  private static final Logger LOG = getLogger(MakePidsAbsoluteUrls.class);


  @Override
  public void execute(TinkerPopGraphManager graphWrapper) throws IOException {
    Transaction tx = graphWrapper.getGraph().tx();
    if (!tx.isOpen()) {
      tx.open();
    }

    graphWrapper.getGraph().traversal()
                .V()
                .has("pid")
                .filter(x -> !((String) x.get().value("pid")).startsWith("http://"))
                .forEachRemaining(it -> {
                  String orig = it.value("pid");
                  String replacement = "http://hdl.handle.net/11240/" + orig;
                  if (Math.random() < 0.1) {
                    LOG.info(Logmarkers.migration, "Replacing " + orig + " with " + replacement);
                  }
                  it.property("pid", replacement);
                });
    tx.commit();
  }
}
