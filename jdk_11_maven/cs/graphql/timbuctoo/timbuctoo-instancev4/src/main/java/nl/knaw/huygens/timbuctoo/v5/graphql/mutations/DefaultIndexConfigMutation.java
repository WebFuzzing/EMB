package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ImmutableContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.replace;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.value;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HASINDEXERCONFIG;

public class DefaultIndexConfigMutation extends Mutation {
  private static String INDEX_TITLE = "{\n" +
      "\t\"facet\": [],\n" +
      "\t\"fullText\": [{\n" +
      "\t\t\"fields\": [{\n" +
      "\t\t\t\"path\": \"[[\\\"%s\\\", \\\"title\\\"], [\\\"Value\\\", \\\"value\\\"]]\"\n" +
      "\t\t}]\n" +
      "\t}]\n" +
      "}";
  private final DataSetRepository dataSetRepository;
  private final String dataSetName;
  private final String ownerId;

  public DefaultIndexConfigMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository, String dataSetId) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
    Tuple<String, String> dataSetIdSplit = DataSetMetaData.splitCombinedId(dataSetId);
    dataSetName = dataSetIdSplit.getRight();
    ownerId = dataSetIdSplit.getLeft();
  }

  @Override
  protected Object executeAction(DataFetchingEnvironment env) {
    final User user = MutationHelpers.getUser(env);
    Optional<DataSet> dataSetOpt = dataSetRepository.getDataSet(user, ownerId, dataSetName);
    if (!dataSetOpt.isPresent()) {
      throw new RuntimeException("Dataset does not exist");
    }

    final DataSet dataSet = dataSetOpt.get();
    ImmutableContextData contextData = env.getContext();
    if (!contextData.getUserPermissionCheck().hasPermission(dataSet.getMetadata(), Permission.CONFIG_INDEX)) {
      throw new RuntimeException("User has no permissions to change the index configuration.");
    }

    final ReadOnlyChecker readOnlyChecker = dataSet.getReadOnlyChecker();
    final PredicateMutation predicateMutation = new PredicateMutation();
    final TypeNameStore typeNameStore = dataSet.getTypeNameStore();
    final PredicateMutation[] resetIndexPredicates = dataSet
        .getSchemaStore().getStableTypes().values().stream()
        .map(Type::getName)
        // The read-only data is non user data, where we do not need an search index for.
        .filter(uri -> !readOnlyChecker.isReadonlyType(uri))
        .map(collectionUri -> createIndexPredicate(predicateMutation, typeNameStore, collectionUri))
        .toArray(PredicateMutation[]::new);

    try {
      MutationHelpers.addMutations(dataSet, resetIndexPredicates);
    } catch (LogStorageFailedException | ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    return ImmutableMap.of("message", "Index is rest.");
  }

  private PredicateMutation createIndexPredicate(PredicateMutation predicateMutation, TypeNameStore typeNameStore,
                                                 String collectionUri) {
    final String indexConfig = String.format(INDEX_TITLE, typeNameStore.makeGraphQlname(collectionUri));
    return predicateMutation.entity(collectionUri, replace(TIM_HASINDEXERCONFIG, value(indexConfig)));
  }
}
