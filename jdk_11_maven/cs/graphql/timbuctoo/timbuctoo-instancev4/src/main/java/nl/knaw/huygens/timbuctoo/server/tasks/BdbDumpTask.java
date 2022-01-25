package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.collect.Sets;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.LockMode;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbEnvironmentCreator;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BdbDumpTask extends Task {

  private static final String OWNER = "ownerId";
  private static final String DATA_SET = "dataSetName";
  private static final String DATABASE = "databaseName";
  private static final HashSet<String> REQUIRED_PARAMS = Sets.newHashSet(OWNER, DATA_SET, DATABASE);
  private static final TupleBinding<String> STRING_BINDER = TupleBinding
    .getPrimitiveBinding(String.class);
  private final BdbEnvironmentCreator environmentCreator;

  public BdbDumpTask(BdbEnvironmentCreator environmentCreator) {
    super("dbdump");
    this.environmentCreator = environmentCreator;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    if (!parameters.keySet().containsAll(REQUIRED_PARAMS)) {
      output.write("Make sure you provide the following parameters: " + REQUIRED_PARAMS);
      return;
    }

    BdbWrapper<String, String> database = environmentCreator.getDatabase(
      getParam(parameters, OWNER),
      getParam(parameters, DATA_SET),
      getParam(parameters, DATABASE),
      true,
      STRING_BINDER,
      STRING_BINDER,
      new StringStringIsCleanHandler()
    );

    String prefix = parameters.containsKey("prefix") ? getParam(parameters, "prefix") : "";
    int start = parameters.containsKey("start") ? Integer.parseInt(getParam(parameters, "start")) : 0;
    int count = parameters.containsKey("count") ? Integer.parseInt(getParam(parameters, "count")) : 10;
    output.write("uncommitted data: " + database.dump(prefix, start, count, LockMode.READ_UNCOMMITTED));
    output.write("committed data: " + database.dump(prefix, start, count, LockMode.READ_COMMITTED));
  }

  private String getParam(Map<String, List<String>> parameters, String key) {
    return parameters.get(key).get(0);
  }

}
