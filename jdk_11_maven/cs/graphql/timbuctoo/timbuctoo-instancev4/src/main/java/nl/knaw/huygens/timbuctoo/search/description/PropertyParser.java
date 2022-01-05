package nl.knaw.huygens.timbuctoo.search.description;

public interface PropertyParser {
  String parse(String value);

  Comparable<?> parseForSort(String value);
}
