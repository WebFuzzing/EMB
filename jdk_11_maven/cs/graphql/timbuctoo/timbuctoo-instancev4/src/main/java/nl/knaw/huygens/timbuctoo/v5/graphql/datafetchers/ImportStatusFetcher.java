package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.LogEntryImportStatus;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.MutationHelpers;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.util.List;
import java.util.Optional;

import static java.util.stream.StreamSupport.stream;

public class ImportStatusFetcher implements DataFetcher<LogEntryImportStatus> {
  private final DataSetRepository dataSetRepository;

  public ImportStatusFetcher(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public LogEntryImportStatus get(DataFetchingEnvironment env) {
    User currentUser = env.<ContextData>getContext().getUser()
                                                    .orElseThrow(() -> new RuntimeException("You are not logged in"));

    DataSetMetaData dataSetMetaData = env.getSource();

    MutationHelpers.checkPermission(env, dataSetMetaData, Permission.READ_IMPORT_STATUS);

    Optional<DataSet> dataSetOpt = dataSetRepository.getDataSet(
      currentUser,
      dataSetMetaData.getOwnerId(),
      dataSetMetaData.getDataSetId()
    );

    return dataSetOpt.map(dataSet -> dataSet.getImportManager().getLogList())
                     .map(logList -> {
                       int id = Integer.parseInt(env.getArgument("id"));
                       List<LogEntry> entries = logList.getEntries();
                       if (entries.size() > id) {
                         LogEntry logEntry = logList.getEntries().get(id);
                         Iterable<LogEntry> unprocessedEntries = logList::getUnprocessed;
                         boolean unprocessed = stream(unprocessedEntries.spliterator(), false)
                           .anyMatch(unprocessedEntry -> unprocessedEntry.equals(logEntry));
                         return new LogEntryImportStatus(logEntry, id, unprocessed);
                       }
                       return null;
                     }).orElseThrow(() -> new RuntimeException("No import status with id: " + env.getArgument("id")));

  }
}
