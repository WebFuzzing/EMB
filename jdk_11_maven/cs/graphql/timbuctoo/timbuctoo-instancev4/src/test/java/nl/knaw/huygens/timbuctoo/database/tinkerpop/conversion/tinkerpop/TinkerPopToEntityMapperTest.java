package nl.knaw.huygens.timbuctoo.database.tinkerpop.conversion.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.conversion.TinkerPopToEntityMapper;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TinkerPopToEntityMapperTest {

  @Test
  public void mapEntitySetsABogusTimIdWhenTheVertexHasNoTimId() {
    UUID bogusId = UUID.fromString("0000000-0000-0000-0000-000000000000");

    GraphTraversalSource traversalSource = newGraph()
      .withVertex(v -> v
        .withType("thing")
        .withVre("test")
        .withProperty("isLatest", true)
        .withProperty("deleted", true)
        .withProperty("rev", 1)
      ).build().traversal();
    GraphTraversal<Vertex, Vertex> traversal = traversalSource.V();

    Vres vres = createConfiguration();
    Collection collection = vres.getCollection("testthings").get();
    TinkerPopToEntityMapper instance =
      new TinkerPopToEntityMapper(collection, traversalSource, vres, (entity, entityVertex) -> {
      }, (traversalSource1, vre, target, relationRef) -> {
      });

    ReadEntity readEntity = instance.mapEntity(traversal, false);

    assertThat(readEntity.getId(), is(bogusId));
  }

  private Vres createConfiguration() {
    return new VresBuilder()
      .withVre("testVre", "test", vre -> vre
        .withCollection("testthings", col -> col
          .withProperty("prop1", localProperty("testthing_prop1"))
          .withProperty("prop2", localProperty("testthing_prop2"))
          .withDisplayName(localProperty("testthing_displayName"))
        )
        .withCollection("teststuffs")
        .withCollection("testrelations", CollectionBuilder::isRelationCollection)
        .withCollection("testkeywords", col -> col
          .withDisplayName(localProperty("testkeyword_displayName"))
          .withProperty("type", localProperty("testkeyword_type"))
        )
      )
      .withVre("otherVre", "other", vre -> vre
        .withCollection("otherthings", col -> col
          .withProperty("prop1", localProperty("otherthing_prop1"))
          .withProperty("prop2", localProperty("otherthing_prop2"))
        ))
      .build();
  }
}
