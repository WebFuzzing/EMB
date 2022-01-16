package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;

public class OtherDataSetFetcher implements DataFetcher<List<Map>> {

  private final DataSetRepository repo;

  public OtherDataSetFetcher(DataSetRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<Map> get(DataFetchingEnvironment env) {
    if (env.getSource() instanceof SubjectReference) {
      final Set<String> dataSetIds = new HashSet<>();
      SubjectReference source = env.getSource();
      ContextData contextData = env.getContext();
      Stream<DataSet> dataSets = contextData.getUser()
        .map(user -> repo.getDataSetsWithReadAccess(user).stream())
        .orElseGet(() -> repo.getDataSets().stream().filter(d -> d.getMetadata().isPublished()));

      if (env.containsArgument("dataSetIds")) {
        dataSetIds.addAll(env.getArgument("dataSetIds"));
        dataSets = dataSets.filter(d -> dataSetIds.contains(d.getMetadata().getCombinedId()));
      }
      final String ownId = source.getDataSet().getMetadata().getCombinedId();
      return dataSets
        .filter(d -> !ownId.equals(d.getMetadata().getCombinedId()))
        .map(d -> {
          try (Stream<CursorQuad> quads = d.getQuadStore().getQuads(source.getSubjectUri())) {
            return tuple(d, quads.findAny());
          }
        })
        .filter(i -> i.getRight().isPresent())
        .map(i -> ImmutableMap.of(
          "metadata", new DataSetWithDatabase(i.getLeft(), env.<ContextData>getContext().getUserPermissionCheck()),
          "entity", new LazyTypeSubjectReference(i.getRight().get().getSubject(), i.getLeft())
        ))
        .collect(toList());

    }
    return Lists.newArrayList();
  }

}
