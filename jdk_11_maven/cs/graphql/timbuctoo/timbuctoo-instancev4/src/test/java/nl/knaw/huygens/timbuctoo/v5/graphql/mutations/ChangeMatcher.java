package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import co.unruly.matchers.StreamMatchers;
import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.hamcrest.PropertyMatcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import org.assertj.core.util.Lists;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.empty;

public class ChangeMatcher extends CompositeMatcher<Change> {
  private ChangeMatcher() {

  }

  public static ChangeMatcher likeChange() {
    return new ChangeMatcher();
  }

  public ChangeMatcher withSubject(String subject) {
    this.addMatcher(new PropertyEqualityMatcher<Change, String>("subject", subject) {
      @Override
      protected String getItemValue(Change item) {
        return item.getSubject();
      }
    });
    return this;
  }

  public ChangeMatcher withPredicate(String predicate) {
    this.addMatcher(new PropertyEqualityMatcher<Change, String>("predicate", predicate) {
      @Override
      protected String getItemValue(Change item) {
        return item.getPredicate();
      }
    });
    return this;
  }

  public ChangeMatcher withValues(Value... values) {
    this.addMatcher(new PropertyEqualityMatcher<Change, List<Value>>("values", Lists.newArrayList(values)) {
      @Override
      protected List<Value> getItemValue(Change item) {
        return item.getValues();
      }
    });
    return this;
  }

  public ChangeMatcher withOldValues(Value... oldValues) {
    this.addMatcher(new PropertyMatcher<Change, Stream<Value>>("oldValues", StreamMatchers.contains(oldValues)) {
      @Override
      protected Stream<Value> getItemValue(Change item) {
        return item.getOldValues();
      }
    });
    return this;
  }

  public ChangeMatcher valuesIsEmpty() {
    this.addMatcher(new PropertyMatcher<Change, Collection<? extends Value>>("values", empty()) {
      @Override
      protected Collection<Value> getItemValue(Change item) {
        return item.getValues();
      }
    });
    return this;
  }

  public ChangeMatcher oldValuesIsEmpty() {
    this.addMatcher(new PropertyEqualityMatcher<Change, Boolean>("oldValues", false) {
      @Override
      protected Boolean getItemValue(Change item) {
        return item.getOldValues().findAny().isPresent();
      }
    });
    return this;
  }
}
