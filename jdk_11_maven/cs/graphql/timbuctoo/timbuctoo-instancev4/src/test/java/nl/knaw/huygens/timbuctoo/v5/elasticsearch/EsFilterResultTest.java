package nl.knaw.huygens.timbuctoo.v5.elasticsearch;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class EsFilterResultTest {

  @Test
  public void getNextTokenReturnsATokenWhenTheSizePlusTheCurrentFromAreLessThanTheTotal() {
    final ObjectNode queryNode = jsnO("from", jsn(0), "size", jsn(10));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String nextToken = instance.getNextToken();

    assertThat(nextToken, is(notNullValue()));
  }

  @Test
  public void getNextTokenReturnsNoTokenWhenTheSizePlusTheCurrentFromAreMoreThanTheTotal() {
    final ObjectNode queryNode = jsnO("from", jsn(15), "size", jsn(10));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String nextToken = instance.getNextToken();

    assertThat(nextToken, is(nullValue()));
  }

  @Test
  public void getNextTokenReturnsNoTokenWhenTheSizePlusTheCurrentFromAreEqualToTheTotal() {
    final ObjectNode queryNode = jsnO("from", jsn(10), "size", jsn(10));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String nextToken = instance.getNextToken();

    assertThat(nextToken, is(nullValue()));
  }

  @Test
  public void getPrevTokenReturnsNullWhenCurrentFromIsZero() {
    final ObjectNode queryNode = jsnO("from", jsn(0), "size", jsn(10));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String prevToken = instance.getPrevToken();

    assertThat(prevToken, is(nullValue()));
  }

  @Test
  public void getPrevTokenReturnsNonNullWhenCurrentFromIsBiggerThatZero() {
    final ObjectNode queryNode = jsnO("from", jsn(10), "size", jsn(10));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String prevToken = instance.getPrevToken();

    assertThat(prevToken, is(notNullValue()));
  }

  @Test
  public void getPrevTokenReturnsZeroWhenCurrentFromMinusSizeIsSmallerThanZero() {
    final ObjectNode queryNode = jsnO("from", jsn(10), "size", jsn(12));
    final ObjectNode resultNode = jsnO("hits", jsnO("total", jsn(20)));
    final EsFilterResult instance = new EsFilterResult(queryNode, resultNode);

    final String prevToken = instance.getPrevToken();

    assertThat(prevToken, is("0"));
  }

}
