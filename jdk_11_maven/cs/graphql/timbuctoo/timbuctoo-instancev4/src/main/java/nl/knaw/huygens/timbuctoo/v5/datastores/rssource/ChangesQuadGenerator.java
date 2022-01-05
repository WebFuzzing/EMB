package nl.knaw.huygens.timbuctoo.v5.datastores.rssource;

import com.google.common.base.Charsets;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;

public class ChangesQuadGenerator {
  private final String defaultGraph;

  public ChangesQuadGenerator(String defaultGraph) {
    this.defaultGraph = defaultGraph;
  }

  public String delRelation(String subject, String predicate, String object, String graph) {
    if (graph == null) {
      graph = defaultGraph;
    }
    return "-" + "<" + subject + "> <" + predicate + "> <" + object + "> <" + graph + "> .\n";
  }

  public String delValue(String subject, String predicate, String value, String valueType, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }

    return "-" + "<" + subject + "> <" + predicate + "> \"" + value + "\"^^<" + valueType + "> <" + graph + "> .\n";
  }

  private String escapeCharacters(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\"", "\\\"");
  }

  public String delLanguageTaggedString(String subject, String predicate, String value, String language, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    return "-" + "<" + subject + "> <" + predicate + "> \"" + value + "\"@" + language + " <" + graph + "> .\n";
  }


  public MediaType getMediaType() {
    return new MediaType("application", "vnd.timbuctoo-rdf.nquads_unified_diff");
  }


  public Charset getCharset() {
    return Charsets.UTF_8;
  }


  public String onRelation(String subject, String predicate, String object, String graph) {
    if (graph == null) {
      graph = defaultGraph;
    }
    return "+" + "<" + subject + "> <" + predicate + "> <" + object + "> <" + graph + "> .\n";
  }


  public String onValue(String subject, String predicate, String value, String valueType, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    return "+" + "<" + subject + "> <" + predicate + "> \"" + value + "\"^^<" + valueType + "> <" + graph + "> .\n";
  }


  public String onLanguageTaggedString(String subject, String predicate, String value, String language, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    return "+" + "<" + subject + "> <" + predicate + "> \"" + value + "\"@" + language + " <" + graph + "> .\n";
  }
}
