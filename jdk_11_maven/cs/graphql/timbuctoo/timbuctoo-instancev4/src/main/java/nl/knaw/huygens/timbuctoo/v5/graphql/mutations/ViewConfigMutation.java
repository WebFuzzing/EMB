package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;

import java.util.concurrent.ExecutionException;

import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.replace;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.value;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.HAS_VIEW_CONFIG;

public class ViewConfigMutation extends Mutation {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final DataSetRepository dataSetRepository;

  public ViewConfigMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    String collectionUri = env.getArgument("collectionUri");
    Object viewConfig = env.getArgument("viewConfig");
    DataSet dataSet = MutationHelpers.getDataSet(env, dataSetRepository::getDataSet);

    MutationHelpers.checkPermission(env, dataSet.getMetadata(), Permission.CONFIG_VIEW);
    try {
      MutationHelpers.addMutation(
        dataSet,
        new PredicateMutation()
          .entity(
            collectionUri,
            replace(HAS_VIEW_CONFIG, value(OBJECT_MAPPER.writeValueAsString(viewConfig)))
          )
      );
      return viewConfig;
    } catch (LogStorageFailedException | InterruptedException | ExecutionException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
