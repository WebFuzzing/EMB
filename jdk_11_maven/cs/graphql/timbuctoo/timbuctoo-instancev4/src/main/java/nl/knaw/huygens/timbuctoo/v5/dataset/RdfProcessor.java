package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

public interface RdfProcessor {

  void setPrefix(String prefix, String iri) throws RdfProcessingFailedException;

  void addRelation(String subject, String predicate, String object, String graph)
      throws RdfProcessingFailedException;

  void addValue(String subject, String predicate, String value, String dataType, String graph)
      throws RdfProcessingFailedException;

  void addLanguageTaggedString(String subject, String predicate, String value, String language,
                               String graph) throws RdfProcessingFailedException;

  void delRelation(String subject, String predicate, String object, String graph)
      throws RdfProcessingFailedException;

  void delValue(String subject, String predicate, String value, String valueType, String graph)
      throws RdfProcessingFailedException;

  void delLanguageTaggedString(String subject, String predicate, String value, String language,
                               String graph) throws RdfProcessingFailedException;

  default void onQuad(boolean isAssertion, String subject, String predicate, String object,
                      String dataType, String language, String graph) throws RdfProcessingFailedException {
    if (isAssertion) {
      if (dataType == null || dataType.isEmpty()) {
        this.addRelation(subject, predicate, object, graph);
      } else {
        if (language != null && !language.isEmpty() && dataType.equals(RdfConstants.LANGSTRING)) {
          this.addLanguageTaggedString(subject, predicate, object, language, graph);
        } else {
          this.addValue(subject, predicate, object, dataType, graph);
        }
      }
    } else {
      if (dataType == null || dataType.isEmpty()) {
        this.delRelation(subject, predicate, object, graph);
      } else {
        if (language != null && !language.isEmpty() && dataType.equals(RdfConstants.LANGSTRING)) {
          this.delLanguageTaggedString(subject, predicate, object, language, graph);
        } else {
          this.delValue(subject, predicate, object, dataType, graph);
        }
      }
    }
  }

  void start(int index) throws RdfProcessingFailedException;

  int getCurrentVersion();

  void commit() throws RdfProcessingFailedException;


}
