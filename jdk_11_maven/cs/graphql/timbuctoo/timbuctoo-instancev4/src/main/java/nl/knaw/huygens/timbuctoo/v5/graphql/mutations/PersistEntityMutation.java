package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetchingEnvironment;
import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableEntityLookup;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.GetEntity;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

import java.io.UnsupportedEncodingException;
import java.net.URI;

public class PersistEntityMutation extends Mutation {

  private final RedirectionService redirectionService;
  private final String dataSetId;
  private final String dataSetName;
  private final String ownerId;
  private final UriHelper uriHelper;

  public PersistEntityMutation(Runnable schemaUpdater, RedirectionService redirectionService,
                               String dataSetId, UriHelper uriHelper) {
    super(schemaUpdater);
    this.redirectionService = redirectionService;
    this.dataSetId = dataSetId;
    Tuple<String, String> dataSetIdSplit = DataSetMetaData.splitCombinedId(dataSetId);
    dataSetName = dataSetIdSplit.getRight();
    ownerId = dataSetIdSplit.getLeft();
    this.uriHelper = uriHelper;
  }

  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    User user = MutationHelpers.getUser(env);
    String entityUri = env.getArgument("entityUri");
    URI uri;
    try {
      uri = GetEntity.makeUrl(ownerId, dataSetName, entityUri);
    } catch (UnsupportedEncodingException e) {
      return ImmutableMap.of("message", "Request for presistent Uri failed.");
    }
    URI fullUri = uriHelper.fromResourceUri(uri);

    EntityLookup entityLookup = ImmutableEntityLookup.builder().dataSetId(dataSetId).uri(entityUri).user(user).build();
    redirectionService.add(fullUri, entityLookup);

    return ImmutableMap.of("message", "Request for presistent Uri accepted");
  }
}
