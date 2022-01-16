package nl.knaw.huygens.timbuctoo.v5.dataset;

public interface PredicateHandler {
  void onRelation(String uri);

  void onValue(String value, String dataType);

  void onLanguageTaggedString(String value, String language);
}
