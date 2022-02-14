package nl.knaw.huygens.timbuctoo.v5.dataset;

public interface ReadOnlyChecker {
  boolean isReadonlyPredicate(String predicateIri);

  boolean isReadonlyType(String typeUri);
}
