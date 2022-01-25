package nl.knaw.huygens.timbuctoo.model.vre;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;

public class Vre {
  private static final Logger LOG = LoggerFactory.getLogger(Vre.class);
  public static final String HAS_COLLECTION_RELATION_NAME = "hasCollection";
  public static final String VRE_NAME_PROPERTY_NAME = "name";
  public static final String KEYWORD_TYPES_PROPERTY_NAME = "keywordTypes";
  public static final String DATABASE_LABEL = "VRE";
  public static final String PUBLISH_STATE_PROPERTY_NAME = "publishState";
  public static final String VRE_LABEL_PROPERTY_NAME = "vreLabel";
  public static final String COLOR_CODE_PROPERTY_NAME = "colorCode";
  public static final String PROVENANCE_PROPERTY_NAME = "provenance";
  public static final String DESCRIPTION_PROPERTY_NAME = "description";
  public static final String IMAGE_BLOB_PROPERTY_NAME = "image:blob";
  public static final String IMAGE_MEDIA_TYPE_PROPERTY_NAME = "image:mediaType";
  public static final String IMAGE_REV_PROPERTY_NAME = "image:rev";
  public static final String UPLOADED_FILE_NAME = "uploadedFilename";

  public static final String HAS_PREDICATE_VALUE_TYPE_VERTEX_RELATION_NAME = "hasPredicateValueTypeVertex";

  private final PublishState publishState;
  private final VreMetadata metadata;

  public enum PublishState {
    UPLOADING,
    UPLOAD_FAILED,
    MAPPING_CREATION,
    MAPPING_EXECUTION,
    MAPPING_CREATION_AFTER_ERRORS,
    AVAILABLE
  }

  private final String vreName;
  private Map<String, String> keywordTypes = Maps.newHashMap();
  private final LinkedHashMap<String, Collection> collections = Maps.newLinkedHashMap();

  public Vre(String vreName, Map<String, String> keywordTypes, PublishState publishState, VreMetadata metadata) {
    this.vreName = vreName;
    this.keywordTypes = keywordTypes;
    this.publishState = publishState;
    this.metadata = metadata;
  }

  public Vre(String vreName, Map<String, String> keywordTypes) {
    this(vreName, keywordTypes, PublishState.AVAILABLE, new VreMetadata());
  }

  public Vre(String vreName) {
    this(vreName, Maps.newHashMap());
  }

  public Collection getCollectionForTypeName(String entityTypeName) {
    return collections.get(entityTypeName);
  }

  public Optional<Collection> getCollectionForCollectionName(String collectionName) {
    return this.collections.values().stream()
                           .filter(x -> Objects.equals(x.getCollectionName(), collectionName))
                           .findAny();
  }


  public Set<String> getEntityTypes() {
    return collections.keySet();
  }

  public String getVreName() {
    return vreName;
  }

  public VreMetadata getMetadata() {
    return metadata;
  }

  public Optional<Collection> getImplementerOf(String abstractType) {
    return this.collections.values().stream()
                           .filter(x -> Objects.equals(x.getAbstractType(), abstractType))
                           .findAny();
  }

  public String getOwnType(String... types) {
    Iterator<String> intersection = Sets.intersection(collections.keySet(), Sets.newHashSet(types)).iterator();
    if (intersection.hasNext()) {
      return intersection.next();
    } else {
      return null;
    }
  }

  public Map<String, String> getKeywordTypes() {
    return keywordTypes;
  }

  public Optional<Collection> getRelationCollection() {
    Iterator<Collection> collectionIt = getCollections()
      .entrySet()
      .stream()
      .filter(entry -> entry.getValue().isRelationCollection())
      .map(Map.Entry::getValue)
      .iterator();

    return collectionIt.hasNext() ? Optional.of(collectionIt.next()) : Optional.empty();
  }

  public void addCollection(Collection collection) {
    collections.put(collection.getEntityTypeName(), collection);
  }

  public Map<String, Collection> getCollections() {
    return collections;
  }

  public PublishState getPublishState() {
    return publishState;
  }

  public static Vre load(Vertex vreVertex) {
    final Vre vre = new Vre(
      vreVertex.value(VRE_NAME_PROPERTY_NAME),
      loadKeywordTypes(vreVertex),
      loadPublishState(vreVertex),
      loadMetadata(vreVertex)
    );

    vreVertex.vertices(Direction.OUT, HAS_COLLECTION_RELATION_NAME).forEachRemaining(collectionV -> {
      Collection collection = Collection.load(collectionV, vre);
      vre.addCollection(collection);
    });

    return vre;
  }

  private static VreMetadata loadMetadata(Vertex vreVertex) {
    return VreMetadata.fromVertex(vreVertex);
  }

  private static PublishState loadPublishState(Vertex vreVertex) {
    if (vreVertex.property(PUBLISH_STATE_PROPERTY_NAME).isPresent()) {
      return PublishState.valueOf(vreVertex.<String>property(PUBLISH_STATE_PROPERTY_NAME).value());
    } else {
      // Default to available when publishState property is not set (it is an old VRE)
      return PublishState.AVAILABLE;
    }
  }

  private static Map<String, String> loadKeywordTypes(Vertex vreVertex) {
    if (vreVertex.property(KEYWORD_TYPES_PROPERTY_NAME).isPresent()) {
      final String keywordTypesJson = vreVertex.value(KEYWORD_TYPES_PROPERTY_NAME);
      try {
        return new ObjectMapper().readValue(keywordTypesJson, new TypeReference<Map<String, String>>() { });
      } catch (IOException e) {
        LOG.warn(databaseInvariant, "Cannot deserialize keyword types property {} ", keywordTypesJson);
      }
    }

    return Maps.newHashMap();
  }

  public Vertex save(Graph graph) {
    LOG.info("Persisting vre '{}' to database", vreName);

    Vertex vreVertex = findOrCreateVreVertex(graph);

    saveProperties(this.getKeywordTypes(), vreVertex);

    saveCollections(graph, vreVertex);

    return vreVertex;
  }

  private void saveCollections(Graph graphWrapper, Vertex vreVertex) {
    getCollections().forEach((name, collection) -> {
      LOG.debug("Adding collection {} to VRE {}", name, vreName);
      vreVertex.addEdge(HAS_COLLECTION_RELATION_NAME, collection.save(graphWrapper, vreName));
    });
  }

  private void saveProperties(Map<String, String> keywordTypes, Vertex vreVertex) {
    vreVertex.property(VRE_NAME_PROPERTY_NAME, vreName);
    try {
      if (!keywordTypes.isEmpty()) {
        vreVertex.property(KEYWORD_TYPES_PROPERTY_NAME, new ObjectMapper().writeValueAsString(keywordTypes));
      }
    } catch (JsonProcessingException e) {
      LOG.error("Failed to serialize keyword types to JSON {}", keywordTypes);
    }
  }

  private Vertex findOrCreateVreVertex(Graph graph) {
    // Look for existing VRE vertex
    Vertex vreVertex;
    GraphTraversal<Vertex, Vertex> existing = graph.traversal().V().hasLabel(DATABASE_LABEL)
                                                   .has(VRE_NAME_PROPERTY_NAME, vreName);
    // Create new if does not exist
    if (existing.hasNext()) {
      vreVertex = existing.next();
      LOG.debug("Replacing existing vertex {}.", vreVertex);
    } else {
      vreVertex = graph.addVertex(DATABASE_LABEL);
      LOG.debug("Creating new vertex");
    }
    return vreVertex;
  }

}
