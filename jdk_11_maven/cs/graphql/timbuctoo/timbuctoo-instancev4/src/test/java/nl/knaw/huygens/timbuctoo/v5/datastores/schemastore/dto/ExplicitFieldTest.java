package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExplicitFieldTest {
  @Test
  public void convertToPredicateTransformsExplicitFieldValuesToPredicate() throws Exception {
    Set<String> values = new HashSet<>();
    values.add("value1");
    values.add("value2");
    values.add("value3");

    ExplicitField title = new ExplicitField("test:test", false,
      values, null);

    Predicate predicate = title.convertToPredicate();

    assertThat(predicate.getName(), is("test:test"));
    assertThat(predicate.getValueTypes(), IsMapContaining.hasEntry("value1", 0L));
    assertThat(predicate.getValueTypes(), IsMapContaining.hasEntry("value2", 0L));
    assertThat(predicate.getValueTypes(), IsMapContaining.hasEntry("value3", 0L));
  }

  @Test
  public void convertToPredicateTransformsExplicitFieldReferencesToPredicate() throws Exception {
    Set<String> references = new HashSet<>();
    references.add("reference1");
    references.add("reference2");
    references.add("reference3");

    ExplicitField title = new ExplicitField("test:test", false,
      null, references);

    Predicate predicate = title.convertToPredicate();

    assertThat(predicate.getName(), is("test:test"));
    assertThat(predicate.getReferenceTypes(), IsMapContaining.hasEntry("reference1", 0L));
    assertThat(predicate.getReferenceTypes(), IsMapContaining.hasEntry("reference2", 0L));
    assertThat(predicate.getReferenceTypes(), IsMapContaining.hasEntry("reference3", 0L));
  }

  @Test
  public void convertMakesThePredicateAListPredicateIfTheExplicitFieldIs() {
    ExplicitField field = new ExplicitField("test:test", true, null, null);

    Predicate predicate = field.convertToPredicate();

    assertThat(predicate.isList(), is(true));
  }

  @Test
  public void convertMakesAnExplicitPredicate() {
    ExplicitField field = new ExplicitField("test:test", false, null, null);

    Predicate predicate = field.convertToPredicate();

    assertThat(predicate.isExplicit(), is(true));
  }
}
