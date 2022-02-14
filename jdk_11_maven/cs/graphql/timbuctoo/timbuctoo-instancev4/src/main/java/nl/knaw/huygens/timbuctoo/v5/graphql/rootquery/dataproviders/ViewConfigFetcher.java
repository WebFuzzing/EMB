package nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.HAS_VIEW_CONFIG;

public class ViewConfigFetcher implements DataFetcher {
  private static final Logger LOG = LoggerFactory.getLogger(ViewConfigFetcher.class);
  private final ObjectMapper objectMapper;

  public ViewConfigFetcher(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Object get(DataFetchingEnvironment env) {
    SubjectReference source = env.getSource();
    final DataSet dataSet = source.getDataSet();
    final QuadStore qs = dataSet.getQuadStore();
    final Map<String, Type> schema = dataSet.getSchemaStore().getStableTypes();
    final TypeNameStore typeNameStore = dataSet.getTypeNameStore();
    try (Stream<CursorQuad> quads = qs.getQuads(source.getSubjectUri(), HAS_VIEW_CONFIG, Direction.OUT, "")) {
      return quads.findFirst().flatMap(q -> {
        try {
          return Optional.ofNullable(objectMapper.readValue(q.getObject(), List.class));
        } catch (IOException e) {
          LOG.error("view config is not a valid JSON object", e);
          return Optional.empty();
        }
      }).orElseGet(() -> makeDefaultViewConfig(source.getSubjectUri(), schema, typeNameStore));
    }
  }

  private Map path(ArrayNode path) {
    return ImmutableMap.of(
        "type", "PATH",
        "formatter", ImmutableList.of(),
        "subComponents", ImmutableList.of(),
        "value", path.toString()
    );
  }

  private Map title(Map value) {
    return ImmutableMap.of(
        "type", "TITLE",
        "formatter", ImmutableList.of(),
        "subComponents", ImmutableList.of(value)
    );
  }

  private Map image(Map value) {
    return ImmutableMap.of(
        "type", "IMAGE",
        "formatter", ImmutableList.of(),
        "subComponents", ImmutableList.of(value)
    );
  }

  private Map internalLink(Map href, Map caption) {
    return ImmutableMap.of(
        "type", "INTERNAL_LINK",
        "formatter", ImmutableList.of(),
        "subComponents", ImmutableList.of(href, caption)
    );
  }

  private Map keyValue(String key, Map... subComponents) {
    return ImmutableMap.of(
        "type", "KEYVALUE",
        "formatter", ImmutableList.of(),
        "subComponents", ImmutableList.copyOf(subComponents),
        "value", key
    );
  }

  private ArrayNode pushImm(ArrayNode start, JsonNode... additions) {
    final ArrayNode result = jsnA();
    for (JsonNode jsonNode : start) {
      result.add(jsonNode);
    }
    for (JsonNode addition : additions) {
      result.add(addition);
    }
    return result;
  }

  private ArrayList<Map> makeDefaultViewConfig(String collectionUri, Map<String, Type> schema,
                                               TypeNameStore typeNameStore) {
    ArrayList<Map> result = new ArrayList<>();

    Type collectionType = schema.get(collectionUri);
    if (collectionType == null) {
      LOG.error(
          "The collectionUri " + collectionUri + " does not exist in the schema! (it does contain: [ " +
              schema.keySet().stream().collect(Collectors.joining(", ")) + " ]"
      );
      return result;
    }

    result.add(title(path(jsnA(jsnA(jsn("Entity"), jsn("title")), jsnA(jsn("Value"), jsn("value"))))));
    result.add(image(path(jsnA(jsnA(jsn("Entity"), jsn("image")), jsnA(jsn("Value"), jsn("value"))))));
    result.add(keyValue("uri", path(jsnA(jsnA(jsn("Entity"), jsn("uri"))))));

    final String collectionGraphqlTypeWithoutDataSet = typeNameStore.makeGraphQlname(collectionUri);

    for (Predicate predicate : collectionType.getPredicates()) {
      final String predicateAsGraphqlProp = typeNameStore.makeGraphQlnameForPredicate(
          predicate.getName(),
          predicate.getDirection(),
          predicate.isList()
      );

      ArrayNode predicateReference = jsnA(
          jsnA(jsn(collectionGraphqlTypeWithoutDataSet), jsn(predicateAsGraphqlProp))
      );

      if (predicate.isList()) {
        predicateReference.add(jsnA(jsn("items"), jsn("items")));
      }

      String title = "";
      if (predicate.getDirection() == Direction.IN) {
        title = "⬅︎ ";
      }
      title += typeNameStore.shorten(predicate.getName());


      if (predicate.getReferenceTypes().values().stream().anyMatch(x -> x > 0)) {
        //it's at least sometimes a link
        result.add(keyValue(title, internalLink(
            path(pushImm(predicateReference, jsnA(jsn("Entity"), jsn("uri")))),
            path(pushImm(predicateReference, jsnA(jsn("Entity"), jsn("title")), jsnA(jsn("Value"), jsn("value"))))
        )));
      }

      if (predicate.getValueTypes().values().stream().anyMatch(x -> x > 0)) {
        //it's at least sometimes a normal value
        result.add(keyValue(title, path(pushImm(predicateReference, jsnA(jsn("Value"), jsn("value"))))));
      }
    }

    return result;
  }
}
