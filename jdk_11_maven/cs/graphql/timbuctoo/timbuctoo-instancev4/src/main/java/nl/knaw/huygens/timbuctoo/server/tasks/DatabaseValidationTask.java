package nl.knaw.huygens.timbuctoo.server.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.server.healthchecks.DatabaseValidator;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.time.Clock;
import java.util.List;
import java.util.Map;

public class DatabaseValidationTask extends Task {

  private final DatabaseValidator databaseValidator;
  private final Clock clock;
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseValidationTask.class);
  long lastExecutionTime;
  protected int timeOut;

  public DatabaseValidationTask(DatabaseValidator databaseValidator, Clock clock, int timeOut) {
    super("databasevalidation");
    this.databaseValidator = databaseValidator;
    this.clock = clock;
    this.timeOut = timeOut;
    this.lastExecutionTime = -timeOut - 1;
  }

  @Override
  public synchronized void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    long curTime = clock.millis();
    timeOut = 5000;
    if (parameters.containsKey("force") || curTime - lastExecutionTime > timeOut) {
      writeResult(databaseValidator.check(), output);
      lastExecutionTime = clock.millis();
    } else {
      output.write("Still in cooling off period. Use force to override");
    }
  }

  private void writeResult(ValidationResult result, PrintWriter output) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      String content = objectMapper.writeValueAsString(result);
      output.write(content);
    } catch (JsonProcessingException e) {
      LOG.error("Failed to process ValidationResult as JSON", e);
    }
  }
}
