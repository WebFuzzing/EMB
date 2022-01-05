package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created on 2017-12-08 16:40.
 */
public class ImportStatusTest {

  @Test
  public void jsonSerialization() throws Exception {
    LogList logList = new LogList();
    LogEntry entry = LogEntry.create("baseUri", "defaultGraph", "token");
    logList.addEntry(entry);
    ImportStatus status = new ImportStatus(logList);

    status.start("method", "baseUri");
    assertThat(status.getStatus(), is("Started method"));

    status.addError("This error is recorded in logList", new RuntimeException("list"));
    assertThat(status.getStatus().contains("This error is recorded in logList"), is(true));
    assertThat(logList.getListErrors().get(0).contains("This error is recorded in logList"), is(true));

    status.startEntry(entry);
    assertThat(status.getStatus(), is("Adding entry with token token"));

    status.addError("This error is recorded in logEntry", new RuntimeException("entry"));
    assertThat(status.getStatus().contains("This error is recorded in logEntry"), is(true));
    assertThat(entry.getImportStatus().getErrors().get(0).contains("This error is recorded in logEntry"),
      is(true));

    status.finishEntry();
    status.finishList();

    String json = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .writeValueAsString(status);
    //System.out.println(json);
    assertThat(json.contains("\"@type\" : \"ImportStatus\""), is(true));
  }

}
