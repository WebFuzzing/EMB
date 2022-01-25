package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ImmutableContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.io.IOException;
import java.util.Optional;

public class SetCustomProvenanceMutation extends Mutation {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final DataSetRepository dataSetRepository;
  private final String dataSetName;
  private final String ownerId;

  public SetCustomProvenanceMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository,
                                     String dataSetId) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
    Tuple<String, String> dataSetIdSplit = DataSetMetaData.splitCombinedId(dataSetId);
    dataSetName = dataSetIdSplit.getRight();
    ownerId = dataSetIdSplit.getLeft();
  }

  @Override
  public Object executeAction(DataFetchingEnvironment environment) {
    ImmutableContextData contextData = environment.getContext();
    Optional<User> userOpt = contextData.getUser();
    if (!userOpt.isPresent()) {
      throw new RuntimeException("User should be logged in.");
    }
    User user = userOpt.get();
    Optional<DataSet> dataSetOpt = dataSetRepository.getDataSet(user, ownerId, dataSetName);
    if (!dataSetOpt.isPresent()) {
      throw new RuntimeException("Data set is not available.");
    }

    DataSet dataSet = dataSetOpt.get();
    if (!contextData.getUserPermissionCheck().hasPermission(dataSet.getMetadata(), Permission.SET_CUSTOM_PROV)) {
      throw new RuntimeException("User should have permissions to set the custom provenance.");
    }

    TreeNode customProvenanceNode = OBJECT_MAPPER.valueToTree(environment.getArgument("customProvenance"));
    try {
      CustomProvenance customProvenance = OBJECT_MAPPER.treeToValue(customProvenanceNode, CustomProvenance.class);
      validateCustomProvenance(customProvenance);
      dataSet.setCustomProvenance(customProvenance);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ImmutableMap.of("message", "Custom provenance is set.");
  }

  private void validateCustomProvenance(CustomProvenance customProvenance) {
    for (CustomProvenance.CustomProvenanceValueFieldInput field : customProvenance.getFields()) {
      if (((field.getValueType() == null) && (field.getObject() == null)) ||
        ((field.getValueType() != null) && (field.getObject() != null))) {
        throw new RuntimeException("Specify either a value type or an object.");
      }

      if (field.getObject() != null) {
        validateCustomProvenance(field.getObject());
      }
    }
  }
}
