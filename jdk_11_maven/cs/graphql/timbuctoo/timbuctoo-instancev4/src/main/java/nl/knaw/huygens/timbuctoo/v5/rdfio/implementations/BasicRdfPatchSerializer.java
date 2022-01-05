package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations;

import com.google.common.base.Charsets;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;

import javax.ws.rs.core.MediaType;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class BasicRdfPatchSerializer implements RdfPatchSerializer {
  private final Consumer<String> printer;
  private final String defaultGraph;
  private final PrintWriter printWriter;
  String results = "";

  public BasicRdfPatchSerializer(OutputStream output, String defaultGraph) {
    printWriter = new PrintWriter(new OutputStreamWriter(output, Charsets.UTF_8), true);
    this.printer = s -> printWriter.write(s);
    this.defaultGraph = defaultGraph;
  }

  protected BasicRdfPatchSerializer(Consumer<String> printWriter, String defaultGraph) {
    this.printer = printWriter;
    this.defaultGraph = defaultGraph;
    this.printWriter = null;
  }

  public String getResults() {
    return results;
  }

  @Override
  public void delRelation(String subject, String predicate, String object, String graph)
    throws LogStorageFailedException {
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept("-" + "<" + subject + "> <" + predicate + "> <" + object + "> <" + graph + "> .\n");
  }

  @Override
  public void delValue(String subject, String predicate, String value, String valueType, String graph)
    throws LogStorageFailedException {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept(
      "-" + "<" + subject + "> <" + predicate + "> \"" + value + "\"^^<" + valueType + "> <" + graph + "> .\n"
    );
  }

  private String escapeCharacters(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\"", "\\\"");
  }

  @Override
  public void delLanguageTaggedString(String subject, String predicate, String value, String language, String graph)
    throws LogStorageFailedException {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept(
      "-" + "<" + subject + "> <" + predicate + "> \"" + value + "\"@" + language + " <" + graph + "> .\n"
    );
  }

  @Override
  public MediaType getMediaType() {
    return new MediaType("application", "vnd.timbuctoo-rdf.nquads_unified_diff");
  }

  @Override
  public Charset getCharset() {
    return Charsets.UTF_8;
  }

  @Override
  public void onPrefix(String prefix, String iri) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Unimplemented");
  }

  @Override
  public void onRelation(String subject, String predicate, String object, String graph)
    throws LogStorageFailedException {
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept("+" + "<" + subject + "> <" + predicate + "> <" + object + "> <" + graph + "> .\n");
  }

  @Override
  public void onValue(String subject, String predicate, String value, String valueType, String graph)
    throws LogStorageFailedException {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept(
      "+" + "<" + subject + "> <" + predicate + "> \"" + value + "\"^^<" + valueType + "> <" + graph + "> .\n"
    );
  }

  @Override
  public void onLanguageTaggedString(String subject, String predicate, String value, String language, String graph)
    throws LogStorageFailedException {
    value = escapeCharacters(value);
    if (graph == null) {
      graph = defaultGraph;
    }
    printer.accept(
      "+" + "<" + subject + "> <" + predicate + "> \"" + value + "\"@" + language + " <" + graph + "> .\n"
    );
  }

  @Override
  public void close() throws LogStorageFailedException {
    if (printWriter != null) {
      printWriter.flush();
      printWriter.close();
    }
  }
}
