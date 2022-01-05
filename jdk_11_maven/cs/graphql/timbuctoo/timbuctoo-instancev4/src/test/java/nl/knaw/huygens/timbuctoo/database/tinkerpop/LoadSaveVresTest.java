package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcerStubs;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.vres.DatabaseConfiguredVres;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.HashMap;

import static nl.knaw.huygens.timbuctoo.core.TransactionEnforcerStubs.forGraphWrapper;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


public class LoadSaveVresTest {

  @Test
  public void itLoadsTheConfigurationsFromAGraph() throws JsonProcessingException {
    final HashMap<String, String> keywordTypes = new HashMap<>();
    keywordTypes.put("key", "value");
    final String keywordTypesJson = new ObjectMapper().writeValueAsString(keywordTypes);

    final TinkerPopGraphManager graphManager = newGraph()
      .withVertex("documents", v -> {
        v.withProperty(Collection.COLLECTION_NAME_PROPERTY_NAME, "documents")
          .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "document")
          .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, false);
      })
      .withVertex(v -> {

        v.withLabel(Vre.DATABASE_LABEL)
          .withProperty(Vre.VRE_NAME_PROPERTY_NAME, "VreA")
          .withProperty(Vre.KEYWORD_TYPES_PROPERTY_NAME,
           keywordTypesJson)
          .withOutgoingRelation(Vre.HAS_COLLECTION_RELATION_NAME, "documents");
      })
      .wrap();

    TransactionEnforcer transactionEnforcer = forGraphWrapper(graphManager);
    DatabaseConfiguredVres instance = new DatabaseConfiguredVres(transactionEnforcer);

    assertThat(instance.getVre("VreA"), instanceOf(Vre.class));
    assertThat(instance.getCollection("documents").get(), instanceOf(Collection.class));
    assertThat(instance.getCollectionForType("document").get(), instanceOf(Collection.class));
    assertThat(instance.getVres().get("VreA"), instanceOf(Vre.class));
  }

  @Test
  public void onlyReloadReloadsTheConfigurationsFromTheGraph() throws JsonProcessingException {
    TinkerPopGraphManager graphManager = newGraph()
      .withVertex(v -> {
        v.withLabel(Vre.DATABASE_LABEL)
          .withProperty(Vre.VRE_NAME_PROPERTY_NAME, "VreA");
      })
      .wrap();

    TransactionEnforcer transactionEnforcer = TransactionEnforcerStubs.forGraphWrapper(graphManager);
    DatabaseConfiguredVres instance = new DatabaseConfiguredVres(transactionEnforcer);

    assertThat(instance.getVre("VreA"), instanceOf(Vre.class));
    assertThat(instance.getVre("VreB"), CoreMatchers.equalTo(null));

    // TODO find a clearer way to write this test.
    // This call overrides the GraphManager pointer, so the DatabaseConfiguredVres has an empty database again.
    graphManager = newGraph()
      .withVertex(v -> {
        v.withLabel(Vre.DATABASE_LABEL)
          .withProperty(Vre.VRE_NAME_PROPERTY_NAME, "VreB");
      })
      .wrap();

    assertThat(instance.getVre("VreA"), instanceOf(Vre.class));
    assertThat(instance.getVre("VreB"), CoreMatchers.equalTo(null));

    instance.reload();

    assertThat(instance.getVre("VreB"), instanceOf(Vre.class));
    assertThat(instance.getVre("VreA"), CoreMatchers.equalTo(null));
  }

}
