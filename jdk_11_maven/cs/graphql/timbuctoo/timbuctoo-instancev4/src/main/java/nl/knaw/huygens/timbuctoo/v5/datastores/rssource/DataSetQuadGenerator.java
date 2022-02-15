package nl.knaw.huygens.timbuctoo.v5.datastores.rssource;

import com.google.common.base.Charsets;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;

public class DataSetQuadGenerator {
  private final String defaultGraph;

  public DataSetQuadGenerator(String defaultGraph) {
    this.defaultGraph = defaultGraph;
  }

  private String escapeCharacters(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\"", "\\\"");
  }


  public MediaType getMediaType() {
    return new MediaType("application", "n-quads");
  }


  public Charset getCharset() {
    return Charsets.UTF_8;
  }


  public String onRelation(String subject, String predicate, String object, String graph) {
    if (graph == null) {
      graph = defaultGraph;
    }
    return "<" + subject + "> <" + predicate + "> <" + object + "> <" + graph + "> .\n";
  }


  public String onValue(String subject, String predicate, String value, String valueType, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    return "<" + subject + "> <" + predicate + "> \"" + value + "\"^^<" + valueType + "> <" + graph + "> .\n";
  }


  public String onLanguageTaggedString(String subject, String predicate, String value, String language, String graph) {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    return "<" + subject + "> <" + predicate + "> \"" + value + "\"@" + language + " <" + graph + "> .\n";
  }
}
