package nl.knaw.huygens.timbuctoo.server.tasks;

import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableEntityLookup;
import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * A task to add pids to Women Writers entities, that are lacking one
 * This only applies to the v2.1 data.
 * Shell command: curl -X POST http://{timbuctoo_host}:{admin_port}/tasks/addPidsToWWEntities
 */
public class AddPidsToWomenWritersEntities extends Task {

  private static final Logger LOG = LoggerFactory.getLogger(AddPidsToWomenWritersEntities.class);
  private final GraphWrapper graphWrapper;
  private final RedirectionService redirectionService;
  private final UrlGenerator urlGenerator;

  public AddPidsToWomenWritersEntities(GraphWrapper graphWrapper, RedirectionService redirectionService,
                                       UrlGenerator urlGenerator) {
    super("addPidsToWWEntities");
    this.graphWrapper = graphWrapper;
    this.redirectionService = redirectionService;
    this.urlGenerator = urlGenerator;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    final Graph graph = graphWrapper.getGraph();
    final Transaction tx = graph.tx();
    try {
      tx.open();
      addMissingPidsToEntities(graph, "wwpersons", "wwperson");
      addMissingPidsToEntities(graph, "wwdocuments", "wwdocument");
      tx.commit();
    } catch (Exception e) {
      LOG.error("Redirection uri creation failed", e);
      tx.rollback();
    } finally {
      tx.close();
    }
  }

  private void addMissingPidsToEntities(Graph graph, String collectionName, String entityTypeName) {
    LOG.info("requesting pids for collection: {}", collectionName);
    graph.traversal().V().has(T.label, LabelP.of(entityTypeName)).hasNot("pid").valueMap("tim_id", "rev")
         .forEachRemaining(entity -> {
           LOG.info("{}: {}", entityTypeName, entity);

           final UUID id = UUID.fromString(((List<String>) entity.get("tim_id")).get(0));
           final int rev = ((List<Integer>) entity.get("rev")).get(0);
           final EntityLookup entityLookup = ImmutableEntityLookup.builder()
                                                                  .collection(collectionName)
                                                                  .timId(id)
                                                                  .rev(rev)
                                                                  .build();
           redirectionService.oldAdd(urlGenerator.apply(collectionName, id, rev), entityLookup);
         });
  }
}
