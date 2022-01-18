package nl.knaw.huygens.timbuctoo.v5.rdfio;

import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;

public interface RdfSerializer extends AutoCloseable {

  MediaType getMediaType();

  Charset getCharset();

  void onPrefix(String prefix, String iri) throws LogStorageFailedException;

  void onRelation(String subject, String predicate, String object, String graph) throws LogStorageFailedException;

  void onValue(String subject, String predicate, String value, String valueType, String graph)
      throws LogStorageFailedException;

  void onLanguageTaggedString(String subject, String predicate, String value, String language, String graph)
      throws LogStorageFailedException;

  void close() throws LogStorageFailedException;

  default void onQuad(String subject, String predicate, String object,
                      String dataType, String language, String graph) throws LogStorageFailedException {
    if (dataType == null || dataType.isEmpty()) {
      this.onRelation(subject, predicate, object, graph);
    } else {
      if (language != null && !language.isEmpty() && dataType.equals(RdfConstants.LANGSTRING)) {
        this.onLanguageTaggedString(subject, predicate, object, language, graph);
      } else {
        this.onValue(subject, predicate, object, dataType, graph);
      }
    }
  }
}
