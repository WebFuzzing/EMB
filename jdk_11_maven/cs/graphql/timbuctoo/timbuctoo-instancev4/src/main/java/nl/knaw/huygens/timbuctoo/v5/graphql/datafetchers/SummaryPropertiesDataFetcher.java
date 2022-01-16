package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration.SummaryProp;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SummaryPropertiesDataFetcher implements DataFetcher<Map<String, SummaryProp>> {

  private static final Logger LOG = LoggerFactory.getLogger(SummaryPropertiesDataFetcher.class);
  private final ObjectMapper objectMapper;

  public SummaryPropertiesDataFetcher(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Map<String, SummaryProp> get(DataFetchingEnvironment env) {
    HashMap<String, SummaryProp> summaryPropsMap = new HashMap<>();
    if (env.getSource() instanceof SubjectReference) {
      SubjectReference source = env.getSource();
      DataSet dataSet = source.getDataSet();
      QuadStore quadStore = dataSet.getQuadStore();
      summaryPropsMap.put("title", getData(source, quadStore, RdfConstants.TIM_SUMMARYTITLEPREDICATE));
      summaryPropsMap.put("description", getData(source, quadStore, RdfConstants.TIM_SUMMARYDESCRIPTIONPREDICATE));
      summaryPropsMap.put("image", getData(source, quadStore, RdfConstants.TIM_SUMMARYIMAGEPREDICATE));

    }
    return summaryPropsMap;
  }

  private SummaryProp getData(SubjectReference source, QuadStore quadStore, String predicate) {
    try (Stream<CursorQuad> dataQuads = quadStore.getQuads(source.getSubjectUri(), predicate, Direction.OUT, "")) {
      Optional<String> data = dataQuads.findFirst().map(quad -> quad.getObject());
      if (!data.isPresent()) {
        return null;
      }
      try {
        return objectMapper.readValue(data.get(), SummaryProp.class);
      } catch (IOException e) {
        LOG.error("Could not parse '{}' as SummaryProperty", data);
        return null;
      }
    }
  }


}
