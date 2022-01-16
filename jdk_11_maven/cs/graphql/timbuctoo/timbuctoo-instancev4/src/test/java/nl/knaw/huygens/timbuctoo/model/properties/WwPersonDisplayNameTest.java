package nl.knaw.huygens.timbuctoo.model.properties;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.model.PersonName;
import nl.knaw.huygens.timbuctoo.model.PersonNameComponent;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Test;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WwPersonDisplayNameTest {

  @Test
  public void parsesPersonNamesAsDisplayNameIfPresent() {
    final String expectedName = "person names";

    Graph graph = newGraph()
      .withVertex(v -> v
        .withType("person")
        .withVre("ww")
        .withProperty("wwperson_names", getPersonName("person", "names"))
      ).build();

    WwPersonDisplayName instance = new WwPersonDisplayName();

    Try<JsonNode> result = graph.traversal().V().union(instance.traversalJson()).next();

    assertThat(result.get().asText(), equalTo(expectedName));
  }


  @Test
  public void parsesTempNameAsDisplayNameIfPersonNamesIsEmpty() {
    final String tempName = "temp name";

    Graph graph = newGraph()
      .withVertex(v -> v
        .withType("person")
        .withVre("ww")
        .withProperty("wwperson_names", "{\"list\": []}")
        .withProperty("wwperson_tempName", tempName)
      ).build();

    WwPersonDisplayName instance = new WwPersonDisplayName();

    Try<JsonNode> result = graph.traversal().V().union(instance.traversalJson()).next();

    assertThat(result.get().asText(), equalTo("[TEMP] " + tempName));
  }

  @Test
  public void parsesTempNameAsDisplayNameIfPersonNamesIsNotPresent() {
    final String tempName = "temp name";

    Graph graph = newGraph()
      .withVertex(v -> v
        .withType("person")
        .withVre("ww")
        .withProperty("wwperson_tempName", tempName)
      ).build();

    WwPersonDisplayName instance = new WwPersonDisplayName();

    Try<JsonNode> result = graph.traversal().V().union(instance.traversalJson()).next();

    assertThat(result.get().asText(), equalTo("[TEMP] " + tempName));
  }

  private String getPersonName(String foreName, String surName) {
    PersonName name = new PersonName();
    name.addNameComponent(PersonNameComponent.Type.FORENAME, foreName);
    name.addNameComponent(PersonNameComponent.Type.SURNAME, surName);
    String nameProp;
    try {
      nameProp = new ObjectMapper().writeValueAsString(name);
    } catch (IOException e) {
      nameProp = "";
    }

    return "{\"list\": [" + nameProp + "]}";
  }
}
