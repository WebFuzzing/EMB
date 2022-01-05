package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class PredicateTest {

  @Test(expected = IllegalArgumentException.class)
  public void mergeThrowsExceptionIfPredicateNamesDontMatch() throws Exception {
    Predicate predicate1 = createPredicate("predicate1", Direction.OUT, "testOwner");
    Predicate predicate2 = createPredicate("predicate2", Direction.OUT, "testOwner");

    predicate1.merge(predicate2);
  }

  private Predicate createPredicate(String predicate12, Direction out, String testOwner) {
    Predicate predicate1 = new Predicate(predicate12, out);
    predicate1.setOwner(new Type(testOwner));
    return predicate1;
  }

  @Test(expected = IllegalArgumentException.class)
  public void mergeThrowsExceptionIfPredicateDirectionsDontMatch() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    Predicate predicate2 = createPredicate("predicate", Direction.IN, "testOwner");

    predicate1.merge(predicate2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void mergeThrowsExceptionIfPredicateOwnersDontMatch() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner2");
    predicate1.merge(predicate2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void mergeThrowsExceptionIfPredicateOwnerIsNotSet() throws Exception {
    Predicate predicate1 = new Predicate("predicate", Direction.OUT);
    Predicate predicate2 = new Predicate("predicate", Direction.OUT);
    predicate1.merge(predicate2);
  }

  @Test
  public void mergeMaintainsHasBeenListProperty() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate1.setHasBeenList(true);
    Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate2.setHasBeenList(false);
    Predicate mergedPredicate = predicate1.merge(predicate2);
    assertThat(mergedPredicate.hasBeenList(), Matchers.is(true));
  }

  @Test
  public void mergeSumsListOccurrences() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate1.setHasBeenList(true);
    predicate1.setSubjectsWithThisPredicateAsList(2);
    Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate2.setHasBeenList(false);
    predicate2.setSubjectsWithThisPredicateAsList(3);
    Predicate mergedPredicate = predicate1.merge(predicate2);
    assertThat(mergedPredicate.getSubjectsWithThisPredicateAsList(), Matchers.is(5L));
  }

  @Test
  public void mergeSetsOwner() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate1.setHasBeenList(true);
    predicate1.setSubjectsWithThisPredicateAsList(2);
    Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner");
    predicate2.setHasBeenList(false);
    predicate2.setSubjectsWithThisPredicateAsList(3);
    Predicate mergedPredicate = predicate1.merge(predicate2);
    assertThat(mergedPredicate.getOwner().getName(), Matchers.is("testOwner"));
  }


  @Test
  public void mergeCombinesReferencesMap() throws Exception {
    final Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    Map<String, Long> referenceTypes1 = new HashMap<>();
    referenceTypes1.put("Test Reference", 1L);
    referenceTypes1.put("Test Reference 2", 1L);
    referenceTypes1.put("Test Reference 3", 1L);
    predicate1.setReferenceTypes(referenceTypes1);
    final Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner");
    Map<String, Long> referenceTypes2 = new HashMap<>();
    referenceTypes2.put("Test Reference", 1L);
    referenceTypes2.put("Test Reference 2", 1L);
    predicate2.setReferenceTypes(referenceTypes2);
    Predicate mergedPredicate = predicate1.merge(predicate2);
    assertThat(mergedPredicate.getReferenceTypes(), IsMapContaining.hasEntry("Test Reference", 2L));
    assertThat(mergedPredicate.getReferenceTypes(), IsMapContaining.hasEntry("Test Reference 2", 2L));
    assertThat(mergedPredicate.getReferenceTypes(), IsMapContaining.hasEntry("Test Reference 3", 1L));
  }

  @Test
  public void mergeCombinesValuesMap() throws Exception {
    Predicate predicate1 = createPredicate("predicate", Direction.OUT, "testOwner");
    Map<String, Long> valueTypes1 = new HashMap<>();
    valueTypes1.put("Test Value", 1L);
    valueTypes1.put("Test Value 2", 1L);
    predicate1.setValueTypes(valueTypes1);
    final Predicate predicate2 = createPredicate("predicate", Direction.OUT, "testOwner");
    Map<String, Long> valueTypes2 = new HashMap<>();
    valueTypes2.put("Test Value", 1L);
    valueTypes2.put("Test Value 2", 1L);
    valueTypes2.put("Test Value 3", 1L);
    predicate2.setValueTypes(valueTypes2);
    Predicate mergedPredicate = predicate1.merge(predicate2);
    assertThat(mergedPredicate.getValueTypes(), IsMapContaining.hasEntry("Test Value", 2L));
    assertThat(mergedPredicate.getValueTypes(), IsMapContaining.hasEntry("Test Value 2", 2L));
    assertThat(mergedPredicate.getValueTypes(), IsMapContaining.hasEntry("Test Value 3", 1L));
  }
}
