package nl.knaw.huygens.timbuctoo.server.databasemigration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperations;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.core.dto.RelationType.relationType;
import static nl.knaw.huygens.timbuctoo.server.databasemigration.TinkerPopOperationsForMigrations.forInitDb;
import static nl.knaw.huygens.timbuctoo.server.databasemigration.TinkerPopOperationsForMigrations.getVres;
import static nl.knaw.huygens.timbuctoo.server.databasemigration.TinkerPopOperationsForMigrations.saveRelationTypes;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TinkerPopOperationsForMigrationsTest {
  @Test
  public void itLoadsTheConfigurationsFromAGraph() throws JsonProcessingException {
    final HashMap<String, String> keywordTypes = new HashMap<>();
    keywordTypes.put("key", "value");
    final String keywordTypesJson = new ObjectMapper().writeValueAsString(keywordTypes);

    final TinkerPopGraphManager graphManager = newGraph()
      .withVertex("documents", v ->
         v.withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "documents")
          .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "document")
          .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, false)
      )
      .withVertex(v ->
         v.withLabel(Vre.DATABASE_LABEL)
          .withProperty(Vre.VRE_NAME_PROPERTY_NAME, "VreA")
          .withProperty(Vre.KEYWORD_TYPES_PROPERTY_NAME, keywordTypesJson)
          .withOutgoingRelation(Vre.HAS_COLLECTION_RELATION_NAME, "documents")
      )
      .wrap();

    Vres instance = getVres(graphManager);

    assertThat(instance.getVre("VreA"), instanceOf(Vre.class));
    assertThat(instance.getCollection("documents").get(), instanceOf(Collection.class));
    assertThat(instance.getCollectionForType("document").get(), instanceOf(Collection.class));
    assertThat(instance.getVres().get("VreA"), instanceOf(Vre.class));
  }

  @Test
  public void itSavesRelationTypes() throws JsonProcessingException {
    final TinkerPopGraphManager graphManager = newGraph()
      .wrap();

    saveRelationTypes(graphManager,
      relationType("concept", "hasFirstPerson", "person", "someRel", false, false, false, UUID.randomUUID()),
      relationType("concept", "hasSecondPerson", "person", "otherRe;", false, false, false, UUID.randomUUID())
    );

    assertThat(graphManager.getGraph().traversal().V().count().next(), is(2L));
  }

  @Test
  public void itGIvesYouAnInitDb() throws JsonProcessingException {
    final TinkerPopGraphManager graphManager = newGraph()
      .wrap();

    TinkerPopOperations ops = forInitDb(graphManager);

    assertThat(ops.databaseIsEmptyExceptForMigrations(), is(true));

    ops.initDb(new VresBuilder()
      .withVre("Admin", "", vre -> vre.withCollection("relations", CollectionBuilder::isRelationCollection))
      .build(),
      relationType("concept", "hasFirstPerson", "person", "someRel", false, false, false, UUID.randomUUID())
    );

    assertThat(graphManager.getGraph().traversal().V().count().next(), is(4L));
  }

}
