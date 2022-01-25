package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetPublishException;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakePublicMutation extends Mutation {
  private static final Logger LOG = LoggerFactory.getLogger(MakePublicMutation.class);
  private final DataSetRepository dataSetRepository;

  public MakePublicMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
  }


  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    User user = MutationHelpers.getUser(env);

    DataSet dataSet = MutationHelpers.getDataSet(env, dataSetRepository::getDataSet);

    try {
      dataSetRepository.publishDataSet(user, dataSet.getMetadata().getOwnerId(), dataSet.getMetadata().getDataSetId());
    } catch (DataSetPublishException e) {
      LOG.error("Failed to publish data set", e);
      throw new RuntimeException("Failed to publish data set");
    }
    return new DataSetWithDatabase(dataSet, env.<ContextData>getContext().getUserPermissionCheck());
  }
}
