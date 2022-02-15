package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.core.DataStoreOperations;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import nl.knaw.huygens.timbuctoo.util.TestGraphBuilder;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection.HAS_ARCHETYPE_RELATION_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.HAS_COLLECTION_RELATION_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.KEYWORD_TYPES_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.VRE_NAME_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.VreBuilder.vre;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class LoadSaveVreTest {

  private Tuple<DataStoreOperations, Graph> initGraph() {
    return initGraph(c -> {
    });
  }

  private Tuple<DataStoreOperations, Graph> initGraph(Consumer<TestGraphBuilder> init) {
    TestGraphBuilder testGraphBuilder = newGraph();
    init.accept(testGraphBuilder);
    TinkerPopGraphManager graphManager = testGraphBuilder.wrap();
    return tuple(
      TinkerPopOperationsStubs.forGraphWrapper(graphManager),
      graphManager.getGraph());
  }

  private List<Vertex> save(Vre vre, Tuple<DataStoreOperations, Graph> dataAccess) {
    DataStoreOperations db = dataAccess.getLeft();
    db.saveVre(vre);
    db.success();

    return dataAccess.getRight().traversal().V().toList();

  }

  private Vre load(Tuple<DataStoreOperations, Graph> dataAccess) {
    DataStoreOperations db = dataAccess.getLeft();
    return db.loadVres().getVre("VreName");

  }

  @Test
  public void saveCreatesAVertexForTheVre() {
    final Vre vre = new Vre("VreName");

    final Vertex result = save(vre, initGraph()).get(0);

    assertThat(result, likeVertex()
      .withLabel(Vre.DATABASE_LABEL)
      .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
    );
  }

  @Test
  public void saveReplacesAnExistingVertexForTheVre() {
    final Vre vre = new Vre("VreName");

    final List<Vertex> result = save(vre, initGraph(b -> b.withVertex(
      v -> v
        .withLabel(Vre.DATABASE_LABEL)
        .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
    )));

    assertThat(result.get(0).label(), is(Vre.DATABASE_LABEL));
    assertThat(result.get(0).value(VRE_NAME_PROPERTY_NAME), is("VreName"));
    assertThat(result.size(), is(1));
  }

  @Test
  public void saveAddsKeywordTypesWhenAvailable() throws JsonProcessingException {
    Map<String, String> keyWordTypes = Maps.newHashMap();
    keyWordTypes.put("typeA", "valueA");
    keyWordTypes.put("typeB", "valueB");
    final Vre vre = new Vre("VreName", keyWordTypes);

    final Vertex result = save(vre, initGraph()).get(0);

    assertThat(result.property(KEYWORD_TYPES_PROPERTY_NAME).value(),
      equalTo(new ObjectMapper().writeValueAsString(keyWordTypes))
    );
  }

  @Test
  public void saveAddsVreCollectionsToTheVre() {
    Vre vre = vre("VreName", "prefix")
      .withCollection("prefixpersons")
      .withCollection("prefixdocuments")
      .build();

    Optional<Vertex> vreVertex = save(vre, initGraph())
      .stream()
      .filter(x -> ((Neo4jVertex) x).labels().contains(Vre.DATABASE_LABEL))
      .findAny();

    if (!vreVertex.isPresent()) {
      throw new RuntimeException("No Vre vertex found!");
    } else {
      List<Vertex> edges = Lists.newArrayList(vreVertex.get().vertices(Direction.OUT, HAS_COLLECTION_RELATION_NAME));

      assertThat(edges, containsInAnyOrder(
        likeVertex()
          .withLabel(Collection.DATABASE_LABEL)
          .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "prefixpersons"),
        likeVertex()
          .withLabel(Collection.DATABASE_LABEL)
          .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "prefixdocuments")
      ));
    }
  }

  @Test
  public void loadLoadsAVreFromAVertex() throws JsonProcessingException {
    final Map<String, String> keyWordTypes = new HashMap<>();
    keyWordTypes.put("keyword", "type");
    String stringifiedKeyWordTypes = new ObjectMapper().writeValueAsString(keyWordTypes);

    final Vre instance = load(initGraph(g ->
      g.withVertex(v ->
        v.withLabel(Vre.DATABASE_LABEL)
         .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
         .withProperty(KEYWORD_TYPES_PROPERTY_NAME, stringifiedKeyWordTypes)
      )));

    assertThat(instance.getVreName(), equalTo("VreName"));
    assertThat(instance.getKeywordTypes().get("keyword"), equalTo("type"));
  }

  @Test
  public void loadLoadsTheCollections() throws JsonProcessingException {
    final String entityTypeName = "person";
    final String collectionName = "persons";
    final HashMap<String, String> keywordTypes = new HashMap<>();
    keywordTypes.put("keyword", "type");
    String stringifiedKeyWordTypes = new ObjectMapper().writeValueAsString(keywordTypes);


    final Vre instance = load(initGraph(g -> g
      .withVertex("vre", v -> v
        .withLabel(Vre.DATABASE_LABEL)
        .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
        .withProperty(KEYWORD_TYPES_PROPERTY_NAME, stringifiedKeyWordTypes)
      )
      .withVertex(v -> v
        .withLabel(entityTypeName)
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, collectionName)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, entityTypeName)
        .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, false)
        .withIncomingRelation(HAS_COLLECTION_RELATION_NAME, "vre")
      )));
    final Collection collectionByName = instance.getCollectionForCollectionName(collectionName).get();

    assertThat(instance.getKeywordTypes().get("keyword"), equalTo("type"));
    assertThat(collectionByName, instanceOf(Collection.class));
    assertThat(instance.getCollectionForTypeName(entityTypeName), instanceOf(Collection.class));
    assertThat(instance.getCollections().get(entityTypeName), instanceOf(Collection.class));
    assertThat(instance.getEntityTypes(), contains(
      entityTypeName
    ));
    assertThat(instance.getImplementerOf("person").get(), equalTo(collectionByName));
    assertThat(instance.getOwnType("notmytype", "person"), equalTo(entityTypeName));
  }

  @Test
  public void loadLoadsInheritingCollections() {
    final String entityTypeName = "wwperson";
    final String collectionName = "wwpersons";
    final String archetypeName = "person";
    final String archetypeCollectionName = "persons";

    final Vre instance = load(initGraph(g -> g
      .withVertex("vre", v -> v
        .withLabel(Vre.DATABASE_LABEL)
        .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
      )
      .withVertex("collection", v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, collectionName)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, entityTypeName)
        .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, false)
        .withIncomingRelation(HAS_COLLECTION_RELATION_NAME, "vre")
      )
      .withVertex("archetype", v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, archetypeCollectionName)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, archetypeName)
        .withIncomingRelation(HAS_ARCHETYPE_RELATION_NAME, "collection")
      )));

    assertThat(instance.getImplementerOf("person").get().getEntityTypeName(), equalTo(entityTypeName));
  }

  @Test
  public void loadLoadsRelationCollections() {
    final String entityTypeName = "relation";
    final String collectionName = "relations";


    final Vre instance = load(initGraph(g -> g
      .withVertex("vre", v -> v
        .withLabel(Vre.DATABASE_LABEL)
        .withProperty(VRE_NAME_PROPERTY_NAME, "VreName")
      )
      .withVertex("collection", v -> v
        .withLabel(entityTypeName)
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, collectionName)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, entityTypeName)
        .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, true)
        .withIncomingRelation(HAS_COLLECTION_RELATION_NAME, "vre")
      )));
    final Collection collectionByName = instance.getCollectionForCollectionName(collectionName).get();

    assertThat(instance.getRelationCollection().get(), equalTo(collectionByName));
  }

}
