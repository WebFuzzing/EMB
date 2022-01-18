package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.util.TestGraphBuilder;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionStubs.forEntitytypeName;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class CollectionHasEntityRelationChangeListenerTest {

  @Test
  public void onAddToCollectionCreatesARelation() throws Exception {
    final String[] types = {"person", "wwperson"};
    final String typesJson = new ObjectMapper().writeValueAsString(types);
    final String linkingNodeWwPersonsName = "linking-node-wwpersons";
    final String linkingNodePersonsName = "linking-node-persons";
    final String createThisVertexName = "create-this-vertex";
    final TestGraphBuilder builder = newGraph()
      .withVertex(linkingNodePersonsName, v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", linkingNodePersonsName)
      )
      .withVertex(linkingNodeWwPersonsName, v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", linkingNodeWwPersonsName)
      )
      .withVertex("other", v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", "other")
      )
      .withVertex(v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "person")
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "persons")
        .withOutgoingRelation(Collection.HAS_ENTITY_NODE_RELATION_NAME, linkingNodePersonsName)
      )
      .withVertex(v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "wwperson")
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "wwpersons")
        .withOutgoingRelation(Collection.HAS_ENTITY_NODE_RELATION_NAME, linkingNodeWwPersonsName)
      )
      .withVertex(v -> v
        .withProperty("types", typesJson)
        .withTimId(createThisVertexName)
        .withIncomingRelation(Collection.HAS_ENTITY_RELATION_NAME, linkingNodePersonsName)
      );
    final GraphWrapper graphWrapper = builder.wrap();
    Vertex createdVertex = graphWrapper.getGraph().traversal().V().has("tim_id", createThisVertexName).next();

    CollectionHasEntityRelationChangeListener instance = new CollectionHasEntityRelationChangeListener(graphWrapper);

    instance.onAddToCollection(forEntitytypeName("ww", "person"), Optional.empty(), createdVertex);

    final List<Vertex> actualLinkingNodes = Lists.newArrayList(
      createdVertex.vertices(Direction.IN, Collection.HAS_ENTITY_RELATION_NAME));

    assertThat(actualLinkingNodes, containsInAnyOrder(
      likeVertex().withProperty("check", linkingNodePersonsName),
      likeVertex().withProperty("check", linkingNodeWwPersonsName)
    ));
  }

  @Test
  public void onRemoveFromCollectionRemovesTheRelation() throws Exception {
    final String[] types = {"person"};
    final String typesJson = new ObjectMapper().writeValueAsString(types);
    final String linkingNodeWwPersonsName = "linking-node-wwpersons";
    final String linkingNodePersonsName = "linking-node-persons";
    final String updateThisVertexName = "update-this-vertex";

    final TestGraphBuilder builder = newGraph()
      .withVertex(linkingNodePersonsName, v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", linkingNodePersonsName)
      )
      .withVertex(linkingNodeWwPersonsName, v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", linkingNodeWwPersonsName)
      )
      .withVertex("other", v -> v
        .withLabel(Collection.COLLECTION_ENTITIES_LABEL)
        .withProperty("check", "other")
      )
      .withVertex(v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "person")
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "persons")
        .withOutgoingRelation(Collection.HAS_ENTITY_NODE_RELATION_NAME, linkingNodePersonsName)
      )
      .withVertex(v -> v
        .withLabel(Collection.DATABASE_LABEL)
        .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "wwperson")
        .withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "wwpersons")
        .withOutgoingRelation(Collection.HAS_ENTITY_NODE_RELATION_NAME, linkingNodeWwPersonsName)
      )
      .withVertex(v -> v
        .withProperty("types", typesJson)
        .withTimId(updateThisVertexName)
        .withIncomingRelation(Collection.HAS_ENTITY_RELATION_NAME, linkingNodePersonsName)
        .withIncomingRelation(Collection.HAS_ENTITY_RELATION_NAME, linkingNodeWwPersonsName)
      );
    final GraphWrapper graphWrapper = builder.wrap();

    Vertex updatedVertex = graphWrapper.getGraph().traversal().V().has("tim_id", updateThisVertexName).next();

    CollectionHasEntityRelationChangeListener instance = new CollectionHasEntityRelationChangeListener(graphWrapper);

    instance.onRemoveFromCollection(forEntitytypeName("ww", "person"), Optional.empty(), updatedVertex);

    final List<Vertex> actualLinkingNodes = Lists.newArrayList(
      updatedVertex.vertices(Direction.IN, Collection.HAS_ENTITY_RELATION_NAME)
    );

    assertThat(actualLinkingNodes, contains(
      likeVertex().withProperty("check", linkingNodePersonsName)
    ));
  }

}
