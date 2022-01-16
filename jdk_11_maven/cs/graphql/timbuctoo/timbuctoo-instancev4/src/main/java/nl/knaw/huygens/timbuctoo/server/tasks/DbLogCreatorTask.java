package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.base.Stopwatch;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.databaselog.DatabaseFixer;
import nl.knaw.huygens.timbuctoo.databaselog.DatabaseLog;
import nl.knaw.huygens.timbuctoo.databaselog.GraphLogValidator;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class DbLogCreatorTask extends Task {
  public static final Logger LOG = LoggerFactory.getLogger(DbLogCreatorTask.class);
  private final TinkerPopGraphManager graphManager;
  private final DatabaseLog logGenerator;
  private final DatabaseFixer databaseFixer;
  private final GraphLogValidator graphLogValidator;

  public DbLogCreatorTask(TinkerPopGraphManager graphManager) {
    super("createlog");
    this.graphManager = graphManager;
    logGenerator = new DatabaseLog(graphManager);
    databaseFixer = new DatabaseFixer(graphManager);
    graphLogValidator = new GraphLogValidator(graphManager);
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    // Add the missing Vertices and Edges, before creating a log.
    Stopwatch fixStopwatch = Stopwatch.createStarted();
    databaseFixer.fix();
    LOG.info("Fixing the database took {}", fixStopwatch.stop());

    Stopwatch generateStopwatch = Stopwatch.createStarted();
    logGenerator.generate();
    LOG.info("Log creation took {}", generateStopwatch.stop());

    Stopwatch validateStopWatch = Stopwatch.createStarted();
    graphLogValidator.writeReport(output);
    LOG.info("Log validation took {}", validateStopWatch.stop());
    output.flush();
  }
}
