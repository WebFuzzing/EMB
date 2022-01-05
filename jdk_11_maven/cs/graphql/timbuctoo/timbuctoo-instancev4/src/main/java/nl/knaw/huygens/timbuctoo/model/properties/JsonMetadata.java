package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.logging.Logmarkers;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.domain.Autocomplete;
import nl.knaw.huygens.timbuctoo.util.JsonBuilder;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getProp;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

public class JsonMetadata {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMetadata.class);


  private final Vres metadata;
  private final GraphWrapper graph;

  public JsonMetadata(Vres metadata, GraphWrapper graph) {
    this.metadata = metadata;
    this.graph = graph;
  }

  private JsonNode getForCollection(Collection collection, List<Vertex> relations, boolean withCollectionInfo) {
    ArrayNode result = jsnA();
    collection.getWriteableProperties().forEach((name, prop) -> {
      ObjectNode desc = jsnO(
        "name", jsn(name),
        "type", jsn(prop.getTypeId())
      );

      prop.getOptions().ifPresent(options ->
        desc.set("options", jsnA(options.stream().map(JsonBuilder::jsn)))
      );
      prop.getParts().ifPresent(parts ->
        desc.set("options", jsnA(parts.stream().map(JsonBuilder::jsn)))
      );
      result.add(desc);
    });

    //FIXME add check to vres that certifies that the defined derived relations exist in the database
    String abstractType = collection.getAbstractType();
    Vre vre = collection.getVre();

    Map<String, String> keywordTypes = vre.getKeywordTypes();

    String relationCollectionName = vre
      .getImplementerOf("relation")
      .map(Collection::getCollectionName)
      .orElse(null);

    if (relationCollectionName == null) {
      LOG.warn(Logmarkers.databaseInvariant, "Collection {} seems to have no relationCollections",
        collection.getCollectionName());
    }

    relations.stream()
      .filter(v -> getProp(v, "relationtype_sourceTypeName", String.class).orElse("").equals(abstractType))
      .forEach(v -> {
        String timId = getProp(v, "tim_id", String.class).orElse("<unknown>");
        Optional<String> regularName = getProp(v, "relationtype_regularName", String.class);
        Optional<String> inverseName = getProp(v, "relationtype_inverseName", String.class);
        Optional<String> abstractTargetType = getProp(v, "relationtype_targetTypeName", String.class);
        Optional<String> targetType = abstractTargetType
          .flatMap(typeName -> vre.getImplementerOf(typeName).map(Collection::getCollectionName));

        if (regularName.isPresent() && inverseName.isPresent() && targetType.isPresent()) {
          //special support for keywords:
          URI quickSearchUrl;
          if (abstractTargetType.orElse("").equals("keyword")) {
            quickSearchUrl = Autocomplete.makeUrl(
              targetType.get(),
              Optional.empty(),
              Optional.ofNullable(keywordTypes.get(regularName.get()))
            );
          } else {
            quickSearchUrl = Autocomplete.makeUrl(targetType.get());
          }

          boolean isSymmetric = getProp(v, "relationtype_symmetric", Boolean.class).orElse(false);
          result.add(jsnO(
            "name", jsn(regularName.get()),
            "type", jsn("relation"),
            "quicksearch", jsn(quickSearchUrl.toString()),
            "relation", jsnO(
              //for search
              "direction", isSymmetric ? jsn("BOTH") : jsn("OUT"),
              "outName", jsn(regularName.get()),
              "inName", jsn(inverseName.get()),
              "targetCollection", jsn(targetType.get()),
              //for CRUD
              "relationCollection", jsn(relationCollectionName),
              "relationTypeId", jsn(timId)
            )
          ));
        } else {
          if (!regularName.isPresent() || !inverseName.isPresent() || !abstractTargetType.isPresent()) {
            LOG.error(
              Logmarkers.databaseInvariant,
              "RelationType should have a relationtype_regularName, relationtype_inverseName and " +
                "relationtype_targetTypeName, but one of those is missing for " + v.id()
            );
          }
        }
      });
    relations.stream()
      .filter(v -> {
        final String targetType = getProp(v, "relationtype_targetTypeName", String.class).orElse("");
        final boolean isSymmetric = getProp(v, "relationtype_symmetric", Boolean.class).orElse(false);
        if (isSymmetric) {
          final String sourceType = getProp(v, "relationtype_sourceTypeName", String.class).orElse("");
          return targetType.equals(abstractType) && !sourceType.equals(targetType);
        } else {
          return targetType.equals(abstractType);
        }
      })
      .forEach(v -> {
        String timId = getProp(v, "tim_id", String.class).orElse("<unknown>");
        Optional<String> regularName = getProp(v, "relationtype_regularName", String.class);
        Optional<String> inverseName = getProp(v, "relationtype_inverseName", String.class);
        Optional<String> abstractTargetType = getProp(v, "relationtype_sourceTypeName", String.class);
        Optional<String> targetType = abstractTargetType
          .flatMap(typeName -> vre.getImplementerOf(typeName).map(Collection::getCollectionName));

        if (regularName.isPresent() && inverseName.isPresent() && targetType.isPresent()) {
          //special support for keywords:
          URI quickSearchUrl;
          if (abstractTargetType.orElse("").equals("keyword")) {
            quickSearchUrl = Autocomplete.makeUrl(
              targetType.get(),
              Optional.empty(),
              Optional.ofNullable(keywordTypes.get(regularName.get()))
            );
          } else {
            quickSearchUrl = Autocomplete.makeUrl(targetType.get());
          }
          result.add(jsnO(
            "name", jsn(inverseName.get()),
            "type", jsn("relation"),
            "quicksearch", jsn(quickSearchUrl.toString()),
            "relation", jsnO(
              //for search
              "direction", jsn("IN"),
              "outName", jsn(regularName.get()),
              "inName", jsn(inverseName.get()),
              "targetCollection", jsn(targetType.get()),
              //for CRUD
              "relationCollection", jsn(relationCollectionName),
              "relationTypeId", jsn(timId)
            )
          ));
        } else {
          if (!regularName.isPresent() || !inverseName.isPresent() || !abstractTargetType.isPresent()) {
            LOG.error(
              Logmarkers.databaseInvariant,
              "RelationType should have a relationtype_regularName, relationtype_inverseName and " +
                "relationtype_targetTypeName, but one of those is missing for " + v.id()
            );
          }
        }
      });
    if (withCollectionInfo) {
      return jsnO(
        "collectionName", jsn(collection.getCollectionName()),
        "collectionLabel", jsn(collection.getCollectionLabel()),
        "description", jsn(collection.getDescription()),
        "unknown", jsn(collection.isUnknown()),
        "relationCollection", jsn(collection.isRelationCollection()),
        "archetypeName", jsn(collection.getAbstractType()),
        "properties", result
      );
    } else {
      return result;
    }
  }

  public ObjectNode getForVre(String vreName, boolean withCollectionInfo) {
    final List<Vertex> relations = graph.getGraph().traversal().V()
      .has(T.label, LabelP.of("relationtype"))
      .toList();

    ObjectNode result = jsnO();
    metadata.getVre(vreName)
      .getCollections()
      .forEach((key, coll) ->
        result.set(coll.getCollectionName(), getForCollection(coll, relations, withCollectionInfo)));
    return result;
  }

  public ArrayNode listVres() {
    Stream<JsonNode> vres = metadata.getVres().entrySet().stream()
      .filter(x -> x.getValue().getCollections().size() > 0)
      .map(entry -> jsn(entry.getKey()));

    return jsnA(vres);
  }
}
