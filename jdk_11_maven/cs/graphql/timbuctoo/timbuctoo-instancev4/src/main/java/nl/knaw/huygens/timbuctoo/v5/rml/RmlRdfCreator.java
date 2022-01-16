package nl.knaw.huygens.timbuctoo.v5.rml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.jena.JenaBasedReader;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RmlMappingDocument;
import nl.knaw.huygens.timbuctoo.v5.dataset.PlainRdfCreator;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RmlRdfCreator implements PlainRdfCreator {
  @JsonProperty("baseUri")
  private final String baseUri;
  @JsonProperty("rdfData")
  private final String rdfData;
  private final JenaBasedReader rmlBuilder;

  @JsonCreator
  public RmlRdfCreator(@JsonProperty("baseUri") String baseUri, @JsonProperty("rdfData") String rdfData) {
    this.baseUri = baseUri;
    this.rdfData = rdfData;
    this.rmlBuilder = new JenaBasedReader();
  }

  @Override
  public void sendQuads(RdfSerializer saver, DataSet dataSet, Consumer<String> status)
    throws LogStorageFailedException {
    RdfDataSourceFactory dataSourceFactory = dataSet.getDataSource();

    final Model model = ModelFactory.createDefaultModel();
    try {
      model.read(new ByteArrayInputStream(rdfData.getBytes(StandardCharsets.UTF_8)), null, "JSON-LD");
    } catch (Exception e) {
      throw new LogStorageFailedException(e);
    }

    final RmlMappingDocument rmlMappingDocument = rmlBuilder.fromRdf(model, dataSourceFactory::apply);
    if (rmlMappingDocument.getErrors().size() > 0) {
      throw new LogStorageFailedException(
        "failure: " + String.join("\nfailure: ", rmlMappingDocument.getErrors()) + "\n"
      );
    }
    //FIXME: trigger onprefix for all rml prefixes
    //FIXME: store rml and retrieve it from tripleStore when mapping

    Stream<Quad> triples = rmlMappingDocument.execute(new ReportingErrorHandler(status));
    Iterator<Quad> iterator = triples.iterator();
    while (iterator.hasNext()) {
      Quad triple = iterator.next();
      saver.onQuad(
        triple.getSubject().getUri().get(),
        triple.getPredicate().getUri().get(),
        triple.getObject().getContent(),
        triple.getObject().getLiteralType().orElse(null),
        triple.getObject().getLiteralLanguage().orElse(null),
        baseUri
      );
    }
  }
}
