package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.collect.Lists;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionState;
import nl.knaw.huygens.timbuctoo.crud.CrudServiceFactory;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.JsonCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddTypeToNeo4JVertexTask extends Task {
  public static final Logger LOG = LoggerFactory.getLogger(AddTypeToNeo4JVertexTask.class);
  private static String ID = "id";
  private static String TYPE_TO_ADD = "typeToAdd";
  private static List<String> REQUIRED_PARAMS = Lists.newArrayList(ID, TYPE_TO_ADD);
  private final TransactionEnforcer transactionEnforcer;
  private final CrudServiceFactory crudServiceFactory;

  public AddTypeToNeo4JVertexTask(TransactionEnforcer transactionEnforcer,
                                  CrudServiceFactory crudServiceFactory) {
    super("addTypeToNeo4JVertex");
    this.transactionEnforcer = transactionEnforcer;
    this.crudServiceFactory = crudServiceFactory;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    output.println("keyset: " + parameters.keySet());
    if (!parameters.keySet().containsAll(REQUIRED_PARAMS)) {
      output.println("Make sure you provide the following parameters: " + REQUIRED_PARAMS);
      output.flush();
      return;
    }

    String id = parameters.get(ID).get(0);
    String typeToAdd = parameters.get(TYPE_TO_ADD).get(0);

    transactionEnforcer.execute(timbuctooActions -> {
      JsonCrudService jsonCrudService = crudServiceFactory.newJsonCrudService(timbuctooActions);

      try {
        jsonCrudService.addTypeToEntity(UUID.fromString(id), typeToAdd);
        output.println("Add type " + typeToAdd + " vertex with tim_id " + id);
        LOG.info("Added type {} to vertex with tim_id {}", typeToAdd, id);
        return TransactionState.commit();
      } catch (InvalidCollectionException | NotFoundException e) {
        output.println("Type could not be added: " + e.getMessage());
        LOG.error("Type could not be added", e);
        return TransactionState.rollback();
      }

    });

    output.flush();
  }
}
