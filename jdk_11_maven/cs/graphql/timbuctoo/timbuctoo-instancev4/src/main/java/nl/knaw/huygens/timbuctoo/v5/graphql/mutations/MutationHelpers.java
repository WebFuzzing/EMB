package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class MutationHelpers {

  public static User getUser(DataFetchingEnvironment env) {
    ContextData contextData = env.getContext();

    return contextData.getUser().orElseThrow(() -> new RuntimeException("You are not logged in"));
  }

  public static void checkPermission(DataFetchingEnvironment env, DataSetMetaData dataSetMetaData,
                                     Permission permission)
    throws RuntimeException {
    ContextData contextData = env.getContext();

    UserPermissionCheck userPermissionCheck = contextData.getUserPermissionCheck();
    if (!userPermissionCheck.hasPermission(dataSetMetaData, permission)) {
      throw new RuntimeException("You do not have permission '" + permission + "' for this data set.");
    }
  }

  public static DataSet getDataSet(DataFetchingEnvironment env, DataSetFetcher fetcher) {
    String dataSetId = env.getArgument("dataSetId");
    Tuple<String, String> userAndDataSet = DataSetMetaData.splitCombinedId(dataSetId);

    User user = getUser(env);

    String ownerId = userAndDataSet.getLeft();
    String dataSetName = userAndDataSet.getRight();

    return fetcher.getDataSet(user, ownerId, dataSetName)
      .orElseThrow(() -> new RuntimeException("Dataset does not exist"));
  }

  public static void addMutation(DataSet dataSet, PredicateMutation mutation)
    throws LogStorageFailedException, ExecutionException, InterruptedException {

    final String baseUri = dataSet.getMetadata().getBaseUri();
    final String graph = dataSet.getMetadata().getGraph();
    dataSet.getImportManager().generateLog(baseUri, graph, new PredicateMutationRdfPatcher(mutation))
      .get();
  }

  public static void addMutations(DataSet dataSet, PredicateMutation... mutations)
      throws LogStorageFailedException, ExecutionException, InterruptedException {

    final String baseUri = dataSet.getMetadata().getBaseUri();
    final String graph = dataSet.getMetadata().getGraph();
    dataSet.getImportManager().generateLog(baseUri, graph, new PredicateMutationRdfPatcher(mutations))
           .get();
  }

  public interface DataSetFetcher {
    Optional<DataSet> getDataSet(User user, String ownerId, String dataSetName);
  }
}
