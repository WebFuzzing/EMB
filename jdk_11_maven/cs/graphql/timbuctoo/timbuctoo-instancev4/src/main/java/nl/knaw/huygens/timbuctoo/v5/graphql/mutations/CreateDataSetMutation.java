package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.IllegalDataSetNameException;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.MutationHelpers.getUser;

public class CreateDataSetMutation extends Mutation {
  private static final Logger LOG = LoggerFactory.getLogger(CreateDataSetMutation.class);

  private final DataSetRepository dataSetRepository;

  public CreateDataSetMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    User currentUser = getUser(env);

    String dataSetName = env.getArgument("dataSetName");
    try {
      return new DataSetWithDatabase(
        dataSetRepository.createDataSet(currentUser, dataSetName),
        env.<ContextData>getContext().getUserPermissionCheck()
      );
    } catch (DataStoreCreationException e) {
      LOG.error("Data set creation exception", e);
      throw new RuntimeException("Data set could not be created");
    } catch (IllegalDataSetNameException e) {
      throw new RuntimeException("Data set id is not supported: " + e.getMessage());
    } catch (DataSetCreationException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
