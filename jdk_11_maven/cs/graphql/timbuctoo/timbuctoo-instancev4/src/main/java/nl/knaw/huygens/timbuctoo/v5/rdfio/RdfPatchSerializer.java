package nl.knaw.huygens.timbuctoo.v5.rdfio;

import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

public interface RdfPatchSerializer extends RdfSerializer {
  void delRelation(String subject, String predicate, String object, String graph) throws LogStorageFailedException;

  void delValue(String subject, String predicate, String value, String valueType, String graph)
    throws LogStorageFailedException;

  void delLanguageTaggedString(String subject, String predicate, String value, String language, String graph)
    throws LogStorageFailedException;

  default void delQuad(String subject, String predicate, String object,
                      String dataType, String language, String graph) throws LogStorageFailedException {
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

  default void addDelQuad(boolean isAddition, String subject, String predicate, String object, String dataType,
                          String language, String graph) throws LogStorageFailedException {
    if (isAddition) {
      onQuad(subject, predicate, object, dataType, language, graph);
    } else {
      delQuad(subject, predicate, object, dataType, language, graph);
    }
  }
}
