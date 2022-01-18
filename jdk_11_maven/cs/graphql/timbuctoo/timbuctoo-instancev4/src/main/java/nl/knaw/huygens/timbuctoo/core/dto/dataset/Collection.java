package nl.knaw.huygens.timbuctoo.core.dto.dataset;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.properties.LocalProperty;
import nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;
import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getProp;

public class Collection {
  public static final String DATABASE_LABEL = "collection";
  public static final String COLLECTION_ENTITIES_LABEL = "collectionEntities";
  public static final String COLLECTION_NAME_PROPERTY_NAME = "collectionName";
  public static final String COLLECTION_IS_UNKNOWN_PROPERTY_NAME = "isUnknownCollection";
  public static final String COLLECTION_LABEL_PROPERTY_NAME = "collectionLabel";
  public static final String ENTITY_TYPE_NAME_PROPERTY_NAME = "entityTypeName";
  public static final String HAS_ENTITY_NODE_RELATION_NAME = "hasEntityNode";
  public static final String HAS_PROPERTY_RELATION_NAME = "hasProperty";
  public static final String HAS_DISPLAY_NAME_RELATION_NAME = "hasDisplayName";
  public static final String HAS_ENTITY_RELATION_NAME = "hasEntity";
  public static final String HAS_INITIAL_PROPERTY_RELATION_NAME = "hasInitialProperty";
  public static final String HAS_ARCHETYPE_RELATION_NAME = "hasArchetype";
  public static final String IS_RELATION_COLLECTION_PROPERTY_NAME = "isRelationCollection";

  public static final String HAS_PREDICATE_RELATION_NAME = "hasPredicate";
  public static final String HAS_VALUE_TYPE_RELATION_NAME = "hasValueType";
  public static final String APPLIES_TO_RELATION_NAME = "appliesTo";

  private static final Logger LOG = LoggerFactory.getLogger(Collection.class);

  public static final String COLLECTION_DESCRIPTION_PROPERTY_NAME = "collectionDescription";


  private final String entityTypeName;
  private final String collectionName;
  private final Vre vre;
  private final String abstractType;
  private final ReadableProperty displayName;
  private final LinkedHashMap<String, ReadableProperty> properties;
  private final LinkedHashMap<String, LocalProperty> writeableProperties;
  private final boolean isRelationCollection;
  private final String collectionLabel;
  private boolean unknown;
  private final String description;

  //FIXME: shouldn't be public
  public Collection(@NotNull String entityTypeName, @NotNull String abstractType,
                    @NotNull ReadableProperty displayName, @NotNull LinkedHashMap<String, ReadableProperty> properties,
                    @NotNull String collectionName, @NotNull Vre vre, @NotNull String collectionLabel, boolean unknown,
                    boolean isRelationCollection, String description) {
    this.entityTypeName = entityTypeName;
    this.abstractType = abstractType;
    this.displayName = displayName;
    this.properties = properties;
    this.collectionName = collectionName;
    this.vre = vre;
    this.unknown = unknown;
    this.description = description;
    if (collectionLabel == null) {
      this.collectionLabel = collectionName;
    } else {
      this.collectionLabel = collectionLabel;
    }
    this.isRelationCollection = isRelationCollection;
    writeableProperties = properties.entrySet().stream()
                                    .filter(e -> e.getValue() instanceof LocalProperty)
                                    .collect(toMap(
                                      Map.Entry::getKey,
                                      e -> (LocalProperty) e.getValue(),
                                      (v1, v2) -> {
                                        throw new IllegalStateException("Duplicate key");
                                      },
                                      LinkedHashMap::new
                                    ));
  }

  public String getEntityTypeName() {
    return entityTypeName;
  }

  public String getAbstractType() {
    return abstractType;
  }

  public ReadableProperty getDisplayName() {
    return displayName;
  }

  public Map<String, LocalProperty> getWriteableProperties() {
    return writeableProperties;
  }

  public Map<String, ReadableProperty> getReadableProperties() {
    return properties;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public String getCollectionLabel() {
    return collectionLabel;
  }

  public Vre getVre() {
    return vre;
  }

  //A Vre implies a transitive closure to all other collections.
  public String getVreName() {
    return vre.getVreName();
  }

  public boolean isRelationCollection() {
    return isRelationCollection;
  }

  public static Collection load(Vertex collectionVertex, Vre vre) {
    final Vertex archetype = collectionVertex.vertices(Direction.OUT, HAS_ARCHETYPE_RELATION_NAME).hasNext() ?
      collectionVertex.vertices(Direction.OUT, HAS_ARCHETYPE_RELATION_NAME).next() :
      null;

    final String entityTypeName = collectionVertex.value(ENTITY_TYPE_NAME_PROPERTY_NAME);
    final String abstractType = archetype == null ? entityTypeName : archetype.value(ENTITY_TYPE_NAME_PROPERTY_NAME);
    final ReadableProperty displayName = loadDisplayName(collectionVertex, vre);
    final LinkedHashMap<String, ReadableProperty> properties = loadProperties(collectionVertex, vre);
    final String collectionName = collectionVertex.value(COLLECTION_NAME_PROPERTY_NAME);
    final String label = getProp(collectionVertex, COLLECTION_LABEL_PROPERTY_NAME, String.class).orElse(null);
    final String description = getProp(collectionVertex, COLLECTION_DESCRIPTION_PROPERTY_NAME, String.class)
      .orElse(null);

    boolean isRelationCollection = collectionVertex.value(IS_RELATION_COLLECTION_PROPERTY_NAME);
    boolean unknown = getProp(collectionVertex, COLLECTION_IS_UNKNOWN_PROPERTY_NAME, Boolean.class).orElse(false);

    return new Collection(entityTypeName, abstractType, displayName, properties, collectionName, vre,
      label, unknown, isRelationCollection, description);
  }

  private static ReadableProperty loadDisplayName(Vertex collectionVertex, Vre vre) {
    final Iterator<Vertex> initialV = collectionVertex.vertices(Direction.OUT, HAS_DISPLAY_NAME_RELATION_NAME);
    if (!initialV.hasNext()) {
      return null;
    }

    try {
      return ReadableProperty.load(initialV.next(), vre.getVreName());
    } catch (IOException | NoSuchMethodException | InstantiationException | InvocationTargetException |
      IllegalAccessException e) {

      LOG.error(databaseInvariant, "Failed to load configuration for  display name for collection {} ",
        collectionVertex.value(COLLECTION_NAME_PROPERTY_NAME), e);
    }
    return null;
  }

  private static LinkedHashMap<String, ReadableProperty> loadProperties(Vertex collectionVertex, Vre vre) {
    final Iterator<Vertex> initialV = collectionVertex.vertices(Direction.OUT, HAS_INITIAL_PROPERTY_RELATION_NAME);
    final LinkedHashMap<String, ReadableProperty> properties = Maps.newLinkedHashMap();
    if (!initialV.hasNext()) {
      return properties;
    }

    Vertex current = initialV.next();
    try {
      properties.put(current.value(ReadableProperty.CLIENT_PROPERTY_NAME),
        ReadableProperty.load(current, vre.getVreName()));
      while (current.vertices(Direction.OUT, ReadableProperty.HAS_NEXT_PROPERTY_RELATION_NAME).hasNext()) {
        current = current.vertices(Direction.OUT, ReadableProperty.HAS_NEXT_PROPERTY_RELATION_NAME).next();
        properties.put(current.value(ReadableProperty.CLIENT_PROPERTY_NAME),
          ReadableProperty.load(current, vre.getVreName()));
      }

    } catch (IOException | NoSuchMethodException | InstantiationException | InvocationTargetException |
      IllegalAccessException e) {

      LOG.error(databaseInvariant, "Failed to load configuration for property {} for collection {} ",
        current.property(ReadableProperty.CLIENT_PROPERTY_NAME).isPresent() ?
          current.value(ReadableProperty.CLIENT_PROPERTY_NAME) : "",
        collectionVertex.value(COLLECTION_NAME_PROPERTY_NAME), e);
    }

    return properties;
  }

  public Vertex save(Graph graph, String vreName) {
    Vertex collectionVertex = findOrCreateCollectionVertex(graph);

    if (!collectionNameIsUniqueToThisVre(collectionVertex, vreName)) {
      final String message =
        String.format("Collection '%s' already exists for another VRE than '%s'", collectionName, vreName);
      LOG.error(message);
      throw new IllegalArgumentException(message);
    }

    collectionVertex.property(COLLECTION_NAME_PROPERTY_NAME, collectionName);
    collectionVertex.property(ENTITY_TYPE_NAME_PROPERTY_NAME, entityTypeName);
    collectionVertex.property(IS_RELATION_COLLECTION_PROPERTY_NAME, isRelationCollection);
    collectionVertex.property(COLLECTION_LABEL_PROPERTY_NAME, collectionLabel);
    collectionVertex.property(COLLECTION_IS_UNKNOWN_PROPERTY_NAME, unknown);
    if (description != null) {
      collectionVertex.property(COLLECTION_DESCRIPTION_PROPERTY_NAME, description);
    }
    saveArchetypeRelation(graph, collectionVertex);

    savePropertyConfigurations(graph, collectionVertex);

    // Create a container node to hold the entities in this collection.
    Iterator<Vertex> entityNodeIt = collectionVertex.vertices(Direction.OUT, HAS_ENTITY_NODE_RELATION_NAME);
    if (!entityNodeIt.hasNext()) {
      collectionVertex.addEdge(HAS_ENTITY_NODE_RELATION_NAME, graph.addVertex(COLLECTION_ENTITIES_LABEL));
    }

    return collectionVertex;
  }

  private boolean collectionNameIsUniqueToThisVre(Vertex collectionVertex, String vreName) {
    final Iterator<Vertex> vreV = collectionVertex.vertices(Direction.IN, Vre.HAS_COLLECTION_RELATION_NAME);
    if (vreV.hasNext() && !vreV.next().value(Vre.VRE_NAME_PROPERTY_NAME).equals(vreName)) {
      return false;
    }

    return true;
  }


  private void savePropertyConfigurations(Graph graph, Vertex collectionVertex) {

    dropExistingPropertyConfigurations(collectionVertex);

    // Add property configurations
    List<Vertex> propertyVertices = new ArrayList<>();
    writeableProperties.forEach((clientPropertyName, property) -> {
      LOG.debug("Adding property {} to collection {}", clientPropertyName, collectionName);
      final Vertex propertyVertex = property.save(graph, clientPropertyName);
      collectionVertex.addEdge(HAS_PROPERTY_RELATION_NAME, propertyVertex);
      propertyVertices.add(propertyVertex);
    });

    savePropertyConfigurationSortorder(collectionVertex, propertyVertices);

    if (displayName != null) {
      Vertex displayNameVertex = displayName.save(graph, ReadableProperty.DISPLAY_NAME_PROPERTY_NAME);
      collectionVertex.addEdge(HAS_DISPLAY_NAME_RELATION_NAME, displayNameVertex);
    }
  }

  private void savePropertyConfigurationSortorder(Vertex collectionVertex, List<Vertex> propertyVertices) {
    // add hasInitialProperty for sortorder
    if (propertyVertices.size() > 0) {
      collectionVertex.addEdge(HAS_INITIAL_PROPERTY_RELATION_NAME, propertyVertices.get(0));

      // hasNextProperty for sortorder
      Iterator<Vertex> propertyIterator = propertyVertices.iterator();
      Vertex previous = propertyIterator.next();
      Vertex next = propertyIterator.hasNext() ? propertyIterator.next() : null;

      while (next != null) {
        previous.addEdge(ReadableProperty.HAS_NEXT_PROPERTY_RELATION_NAME, next);
        previous = next;
        next = propertyIterator.hasNext() ? propertyIterator.next() : null;
      }
    }
  }

  private void dropExistingPropertyConfigurations(Vertex collectionVertex) {
    collectionVertex
      .vertices(Direction.OUT, HAS_PROPERTY_RELATION_NAME, HAS_DISPLAY_NAME_RELATION_NAME,
        HAS_INITIAL_PROPERTY_RELATION_NAME)
      .forEachRemaining(Element::remove);
  }

  private void saveArchetypeRelation(Graph graph, Vertex collectionVertex) {
    if (!abstractType.equals(entityTypeName)) {
      GraphTraversal<Vertex, Vertex> archetype = graph.traversal().V().hasLabel(DATABASE_LABEL)
                                                      .has(ENTITY_TYPE_NAME_PROPERTY_NAME, abstractType);

      if (!archetype.hasNext()) {
        LOG.error(databaseInvariant, "No archetype collection with entityTypeName {} present in the graph",
          abstractType);
      } else {
        collectionVertex.addEdge(HAS_ARCHETYPE_RELATION_NAME, archetype.next());
      }
    } else {
      LOG.warn("Assuming collection {} is archetype because entityTypeName is equal to abstractType", collectionName);
    }
  }

  private Vertex findOrCreateCollectionVertex(Graph graph) {
    Vertex collectionVertex;
    GraphTraversal<Vertex, Vertex> existing = graph.traversal().V().hasLabel(DATABASE_LABEL)
                                                   .has(COLLECTION_NAME_PROPERTY_NAME, collectionName);

    // Create new if does not exist
    if (existing.hasNext()) {
      collectionVertex = existing.next();
      LOG.debug("Replacing existing vertex {}.", collectionVertex);
    } else {
      collectionVertex = graph.addVertex(DATABASE_LABEL);
      LOG.debug("Creating new vertex");
    }
    return collectionVertex;
  }

  public boolean isUnknown() {
    return unknown;
  }

  public Optional<ReadableProperty> getProperty(String propertyName) {
    return Optional.ofNullable(properties.get(propertyName));
  }

  public String getDescription() {
    return description;
  }
}
