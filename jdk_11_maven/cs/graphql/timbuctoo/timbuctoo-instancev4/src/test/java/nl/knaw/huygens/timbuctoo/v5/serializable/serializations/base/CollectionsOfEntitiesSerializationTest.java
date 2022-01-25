package nl.knaw.huygens.timbuctoo.v5.serializable.serializations.base;

import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.SourceData;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class CollectionsOfEntitiesSerializationTest {

  @Test
  public void performSerialization() throws Exception {
    CollectionsOfEntitiesSerialization sut = new CollectionsOfEntitiesSerialization(){};

    sut.serialize(SourceData.simpleResult());

    assertThat(sut.allEntities.size(), is(3));
    assertThat(sut.allEntities.get("http://example.com/OtherSubItem").size(), is(1));
    assertThat(sut.allEntities.get("http://example.com/Person").size(), is(3));
    assertThat(sut.allEntities.get("http://example.com/SubItem").size(), is(3));

    Map<String, CollectionsOfEntitiesSerialization.AggregatedEntity> persons = sut
      .allEntities.get("http://example.com/Person");
    Set<String> person1BList = persons.get("http://example.com/1").relations.get("http://example.org/b");
    Set<String> person2BList = persons.get("http://example.com/2").relations.get("http://example.org/b");

    assertThat(person1BList, nullValue());//inverse relation

    assertThat(person2BList, containsInAnyOrder(
      "http://example.com/11",
      "http://example.com/12",
      "http://example.com/13"
    ));

    CollectionsOfEntitiesSerialization.AggregatedEntity subItem11 =
      sut.allEntities.get("http://example.com/SubItem").get("http://example.com/11");
    CollectionsOfEntitiesSerialization.AggregatedEntity subItem12 =
      sut.allEntities.get("http://example.com/SubItem").get("http://example.com/12");
    assertThat(subItem11.relations.get("http://example.org/b"), containsInAnyOrder("http://example.com/1"));
    assertThat(subItem12.relations.get("http://example.org/b"), containsInAnyOrder("http://example.com/1"));
  }

}
