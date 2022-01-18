package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LogInfo {
  private LogList logList;

  public LogInfo(LogList logList) {
    this.logList = logList;
  }

  public Date getDateofLastImport() throws ParseException {
    List<LogEntry> entries = logList.getEntries();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.parse(entries.get(entries.size() - 1).getImportStatus().getDate());
  }
}
