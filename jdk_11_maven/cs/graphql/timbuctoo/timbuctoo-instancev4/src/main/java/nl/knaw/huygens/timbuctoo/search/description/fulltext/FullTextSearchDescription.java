package nl.knaw.huygens.timbuctoo.search.description.fulltext;

import nl.knaw.huygens.timbuctoo.search.description.Property;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.FullTextSearchParameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static nl.knaw.huygens.timbuctoo.search.description.Property.derivedProperty;
import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;

public class FullTextSearchDescription {
  public static final ContainsStringPredicate
    CONTAINS_STRING_PREDICATE = new ContainsStringPredicate();
  private final String name;
  private final Property prop1;
  private Property prop2;

  private FullTextSearchDescription(String name, Property prop1, Property prop2) {
    this.name = name;
    this.prop1 = prop1;
    this.prop2 = prop2;
  }

  public static FullTextSearchDescription createLocalSimpleFullTextSearchDescription(
    String name,
    String propertyName) {

    return new FullTextSearchDescription(name,
      localProperty().withName(propertyName).build(), localProperty().withName("").build());
  }

  public static FullTextSearchDescription createLocalFullTextSearchDescriptionWithBackupProperty(
    String name,
    String propertyName,
    String backUpPropertyName
  ) {

    Property prop1 = localProperty().withName(propertyName).build();
    Property prop2 = localProperty().withName(backUpPropertyName).build();
    
    return new FullTextSearchDescription(name, prop1, prop2);
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("unchecked")
  public void filter(GraphTraversal<Vertex, Vertex> traversal, FullTextSearchParameter fullTextSearchParameter) {
    String term = fullTextSearchParameter.getTerm();
    // In the real world, the backup property was never reached due to the .coalesce step being
    // satisfied that the PersonNames' empty list mapped to a blank string.
    // A .union step for both however is quite satisfactory because when a user searches, the user wants
    // to search in all available data.
    traversal.where(__.union(prop1.getTraversal(), prop2.getTraversal())
                      .is(P.test(CONTAINS_STRING_PREDICATE, term)));
  }

  public static FullTextSearchDescription createDerivedFullTextSearchDescriptionWithBackupProperty(
          String name, String propertyName, String backupPropertyName, String... relations) {
    Property prop1 = derivedProperty(relations).withName(propertyName).build();
    Property prop2 = derivedProperty(relations).withName(backupPropertyName).build();

    return new FullTextSearchDescription(name, prop1, prop2);
  }

  private static class ContainsStringPredicate implements BiPredicate {
    @Override
    public boolean test(Object o1, Object o2) {
      if (!(o1 instanceof String)) {
        return false;
      }

      String propertyValue = ((String) o1).toLowerCase();
      String[] matches = Arrays.stream(StringUtils.split((String) o2))
                               .map(String::toLowerCase)
                               .toArray(String[]::new);

      return StringUtils.containsAny(propertyValue, matches);
    }
  }
}
