package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.collect.Lists;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionState;
import nl.knaw.huygens.timbuctoo.crud.CrudServiceFactory;
import nl.knaw.huygens.timbuctoo.crud.JsonCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


/**
 * A task to move edges from an old version of a vertex to the latest one.
 * This only applies to the v2.1 data.
 * Shell command: curl -X POST --data "fromVertex={vertex_id}&toVertex={vertex_id}" http://{timbuctoo_host}:{admin_port}/tasks/moveEdges
 */
public class MoveEdgesTask extends Task {
  public static final Logger LOG = LoggerFactory.getLogger(MoveEdgesTask.class);
  private static final String TO_VERTEX = "toVertex";
  private static final String FROM_VERTEX = "fromVertex";
  private static List<String> REQUIRED_PARAMS = Lists.newArrayList(TO_VERTEX, FROM_VERTEX);

  private final TransactionEnforcer transactionEnforcer;
  private final CrudServiceFactory crudServiceFactory;

  public MoveEdgesTask(TransactionEnforcer transactionEnforcer,
                       CrudServiceFactory crudServiceFactory) {
    super("moveEdges");
    this.transactionEnforcer = transactionEnforcer;
    this.crudServiceFactory = crudServiceFactory;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    if (!parameters.keySet().containsAll(REQUIRED_PARAMS)) {
      output.println("Make sure you provide the following parameters: " + REQUIRED_PARAMS);
      output.flush();
      return;
    }

    final String toVertex = parameters.get(TO_VERTEX).get(0);
    final String fromVertex = parameters.get(FROM_VERTEX).get(0);

    transactionEnforcer.execute(timbuctooActions -> {
      JsonCrudService jsonCrudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
      try {
        jsonCrudService.moveEdges(Integer.parseInt(fromVertex), Integer.parseInt(toVertex));
        output.println("Edges are moved from vertex with id " + fromVertex + "  to vertex with id " + toVertex);
        return TransactionState.commit();
      } catch (NumberFormatException e) {
        LOG.error("{} is not a number: {} or {} is not a number {}", FROM_VERTEX, fromVertex, TO_VERTEX, toVertex);
        output.println("Make sure that " + FROM_VERTEX + " and " + TO_VERTEX + " are integers");
        output.flush();
        return TransactionState.rollback();
      } catch (NotFoundException e) {
        output.println(e.getMessage());
        output.flush();
        return TransactionState.rollback();
      }
    });
  }
}
