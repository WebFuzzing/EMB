package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j;

import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfParser;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.BasicRdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j.parsers.NquadsUdParser.NquadsUdParserFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserRegistry;

import javax.ws.rs.core.MediaType;
import java.io.OutputStream;
import java.util.Set;

import static org.eclipse.rdf4j.rio.Rio.getWriterFormatForMIMEType;

public class Rdf4jIoFactory implements RdfIoFactory {
  public static final Set<RDFFormat> RDF_FORMATS = RDFParserRegistry.getInstance().getKeys();
  private static RdfParser rdfParser = new Rdf4jRdfParser();

  private String rdfFormat = "application/n-quads"; // format for serializer

  public Rdf4jIoFactory() {
    RDFParserRegistry.getInstance().add(new NquadsUdParserFactory());
  }

  @Override
  public RdfParser makeRdfParser(CachedLog log) {
    return rdfParser;
  }

  @Override
  public RdfSerializer makeRdfSerializer(OutputStream output) {
    return new Rdf4jWriter(
      output,
      getWriterFormatForMIMEType(rdfFormat)
        .orElseThrow(() -> new IllegalStateException("Not a known rdf serializer format: " + rdfFormat))
    );
  }

  @Override
  public RdfPatchSerializer makeRdfPatchSerializer(OutputStream output, String baseUri) {
    return new BasicRdfPatchSerializer(output, baseUri);
  }

  @Override
  public boolean isRdfTypeSupported(MediaType mediaType) {
    if (mediaType == null) {
      return false;
    }

    return RDF_FORMATS.stream().anyMatch(format -> format.hasMIMEType(mediaType.toString()));
  }

  public String getRdfFormat() {
    return rdfFormat;
  }

  public void setRdfFormat(String rdfFormat) {
    this.rdfFormat = rdfFormat;
  }
}
