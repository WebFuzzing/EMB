package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.google.common.collect.Lists;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class DataSetImportStatusFetcher implements DataFetcher<List<LogEntryImportStatus>> {

  private final DataSetRepository dataSetRepository;

  public DataSetImportStatusFetcher(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public List<LogEntryImportStatus> get(DataFetchingEnvironment env) {
    User currentUser = env.<ContextData>getContext().getUser()
                                                    .orElseThrow(() -> new RuntimeException("You are not logged in"));

    DataSetMetaData dataSetMetaData = env.getSource();

    MutationHelpers.checkPermission(env, dataSetMetaData, Permission.READ_IMPORT_STATUS);

    DataSetMetaData input = env.getSource();
    Optional<DataSet> dataSetOpt = dataSetRepository.getDataSet(
      currentUser,
      input.getOwnerId(),
      input.getDataSetId()
    );

    return dataSetOpt.map(dataSet -> dataSet.getImportManager().getLogList())
                     .map(logList -> {
                       final int[] num = new int[]{0};
                       return logList.getEntries().stream().map(logEntry -> {
                         Iterable<LogEntry> unprocessedEntries = logList::getUnprocessed;
                         boolean unprocessed = stream(unprocessedEntries.spliterator(), false)
                           .anyMatch(unprocessedEntry -> unprocessedEntry.equals(logEntry));
                         return new LogEntryImportStatus(logEntry, num[0]++, unprocessed);
                       }).collect(toList());
                     }).orElse(Lists.newArrayList());
  }

}
