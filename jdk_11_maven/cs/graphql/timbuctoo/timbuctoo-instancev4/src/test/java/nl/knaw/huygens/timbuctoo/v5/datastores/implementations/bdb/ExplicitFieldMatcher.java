package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;

import java.util.List;
import java.util.Set;

public class ExplicitFieldMatcher extends CompositeMatcher<ExplicitField> {
  private ExplicitFieldMatcher() {
  }

  public static ExplicitFieldMatcher explicitField() {
    return new ExplicitFieldMatcher();
  }


  public ExplicitFieldMatcher withUri(String uri) {
    this.addMatcher(new PropertyEqualityMatcher<ExplicitField, String>("uri", uri) {
      @Override
      protected String getItemValue(ExplicitField item) {
        return item.getUri();
      }
    });

    return this;
  }

  public ExplicitFieldMatcher withIsList(boolean isList) {
    this.addMatcher(new PropertyEqualityMatcher<ExplicitField, Boolean>("isList", isList) {
      @Override
      protected Boolean getItemValue(ExplicitField item) {
        return item.isList();
      }
    });

    return this;
  }

  public ExplicitFieldMatcher withValues(Set<String> values) {
    this.addMatcher(new PropertyEqualityMatcher<ExplicitField, Set<String>>("values", values) {
      @Override
      protected Set<String> getItemValue(ExplicitField item) {
        return item.getValues();
      }
    });

    return this;
  }

  public ExplicitFieldMatcher withReferences(Set<String> references) {
    this.addMatcher(new PropertyEqualityMatcher<ExplicitField, Set<String>>("references", references) {
      @Override
      protected Set<String> getItemValue(ExplicitField item) {
        return item.getReferences();
      }
    });

    return this;
  }
}
