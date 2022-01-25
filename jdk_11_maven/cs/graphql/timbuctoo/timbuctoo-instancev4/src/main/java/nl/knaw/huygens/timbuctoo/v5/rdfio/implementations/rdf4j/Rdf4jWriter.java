package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j;

import com.google.common.base.Charsets;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import javax.ws.rs.core.MediaType;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class Rdf4jWriter implements RdfSerializer {
  public static final Charset UTF_8 = Charsets.UTF_8;
  protected final RDFWriter rdfWriter;
  private final RDFFormat rdfFormat;
  protected final SimpleValueFactory valueFactory;
  protected final Writer writer;

  public Rdf4jWriter(OutputStream outputStream, RDFFormat rdfFormat) {
    writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
    rdfWriter = Rio.createWriter(rdfFormat, writer);
    this.rdfFormat = rdfFormat;
    valueFactory = SimpleValueFactory.getInstance();
    rdfWriter.startRDF();
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.valueOf(rdfFormat.getDefaultMIMEType());
  }

  @Override
  public Charset getCharset() {
    return UTF_8;
  }

  @Override
  public void onPrefix(String prefix, String iri) {
    rdfWriter.handleNamespace(prefix, iri);
  }

  private Resource makeResource(String input) {
    if (input.startsWith("_:")) {
      return valueFactory.createBNode(input.substring(2));
    } else {
      return valueFactory.createIRI(input);
    }
  }


  @Override
  public void onRelation(String subject, String predicate, String object, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        makeResource(object),
        makeResource(graph)
      )
    );
  }

  @Override
  public void onValue(String subject, String predicate, String value, String valueType, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        valueFactory.createLiteral(value, valueFactory.createIRI(valueType)),
        makeResource(graph)
      )
    );
  }

  @Override
  public void onLanguageTaggedString(String subject, String predicate, String value, String language, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        valueFactory.createLiteral(value, language),
        makeResource(graph)
      )
    );
  }

  @Override
  public void close() {
    rdfWriter.endRDF();
  }
}
