package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck.OldGraphQlPermission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;

import java.util.stream.Stream;

public class DataMetaDataListFetcher implements DataFetcher {

  private final DataSetRepository dataSetRepository;

  public DataMetaDataListFetcher(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public Object get(DataFetchingEnvironment env) {
    return (Iterable) () -> {
      Stream<DataSetWithDatabase> dataSets = dataSetRepository.getDataSets()
                                                              .stream()
                                                              .map(dataSet -> new DataSetWithDatabase(dataSet,
                                                                ((ContextData) env.getContext())
                                                                  .getUserPermissionCheck()));
      if (env.getArgument("ownOnly")) {
        String userId =
          ((ContextData) env.getContext()).getUser().map(u -> "u" + u.getPersistentId()).orElse(null);
        dataSets = dataSets.filter(d -> d.getOwnerId().equals(userId));
      }
      // translate
      OldGraphQlPermission permission = OldGraphQlPermission.valueOf(env.getArgument("permission"));
      dataSets = dataSets.filter(d -> ((ContextData) env.getContext()).getUserPermissionCheck()
                                                                      .hasOldGraphQlPermission(d, permission));

      return dataSets.iterator();
    };
  }
}
