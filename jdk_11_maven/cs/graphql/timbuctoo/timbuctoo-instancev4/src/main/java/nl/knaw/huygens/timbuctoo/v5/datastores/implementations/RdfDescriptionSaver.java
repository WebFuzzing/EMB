package nl.knaw.huygens.timbuctoo.v5.datastores.implementations;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.RdfProcessor;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class RdfDescriptionSaver implements RdfProcessor {
  private final String baseUri;
  private final File descriptionFile;
  private Model model;
  private InputStream inputStream;
  private static final List<String> DESCRIPTION_PREDICATES = Lists.newArrayList(
    "http://purl.org/dc/terms",
    "http://schema.org"
  );
  private int currentVersion;
  private ImportStatus importStatus;


  public RdfDescriptionSaver(File descriptionFile, String baseUri, ImportStatus importStatus) throws IOException,
    ParserConfigurationException, SAXException {
    this.baseUri = baseUri;
    descriptionFile.createNewFile();
    this.descriptionFile = descriptionFile;
    inputStream = new FileInputStream(descriptionFile);
    if (inputStream.available() > 0) {
      model = Rio.parse(inputStream, baseUri, RDFFormat.RDFXML);
    } else {
      model = new TreeModel();
    }
    this.importStatus = importStatus;
  }

  private boolean isDescriptionPredicate(String predicate) {
    String predicatePrefix = predicate.substring(0, predicate.lastIndexOf("/"));

    return (DESCRIPTION_PREDICATES.contains(predicatePrefix));
  }

  @Override
  public void setPrefix(String prefix, String iri) throws RdfProcessingFailedException {

  }

  @Override
  public void addValue(String subject, String predicate, String value, String dataType, String graph)
    throws RdfProcessingFailedException {

    try {
      if (Objects.equals(subject, baseUri) && isDescriptionPredicate(predicate)) {

        ValueFactory vf = SimpleValueFactory.getInstance();

        model.add(vf.createIRI(subject), vf.createIRI(predicate), vf.createLiteral(value));

      }
    } catch (Exception e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public void addRelation(String subject, String predicate, String object, String graph)
    throws RdfProcessingFailedException {

    try {
      if (Objects.equals(subject, baseUri) && isDescriptionPredicate(predicate)) {

        ValueFactory vf = SimpleValueFactory.getInstance();

        model.add(vf.createIRI(subject), vf.createIRI(predicate), vf.createIRI(object));
      }
    } catch (Exception e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public void addLanguageTaggedString(String subject, String predicate, String value,
                                      String language, String graph) throws RdfProcessingFailedException {

  }

  @Override
  public void delRelation(String subject, String predicate, String object, String graph)
    throws RdfProcessingFailedException {
    try {
      if (Objects.equals(subject, baseUri)) {
        ValueFactory vf = SimpleValueFactory.getInstance();

        model.remove(vf.createIRI(subject), vf.createIRI(predicate), vf.createIRI(object));
      }
    } catch (Exception e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public void delValue(String subject, String predicate, String value, String dataType, String graph)
    throws RdfProcessingFailedException {
    try {
      if (Objects.equals(subject, baseUri)) {
        ValueFactory vf = SimpleValueFactory.getInstance();

        model.remove(vf.createIRI(subject), vf.createIRI(predicate), vf.createLiteral(value));
      }
    } catch (Exception e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public void delLanguageTaggedString(String subject, String predicate, String value,
                                      String language, String graph) throws RdfProcessingFailedException {

  }

  @Override
  public void start(int index) throws RdfProcessingFailedException {
    this.currentVersion = index;
    importStatus.setStatus("Started " + this.getClass().getSimpleName());
  }

  @Override
  public void commit() throws RdfProcessingFailedException {
    try {
      inputStream.close();
      FileWriter newFileWriter = new FileWriter(descriptionFile, false); //false for overwrite file (true=concat)
      Rio.write(model, newFileWriter, RDFFormat.RDFXML);
      newFileWriter.flush();
      newFileWriter.close();
      importStatus.setStatus("Description saved");
    } catch (IOException e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public int getCurrentVersion() {
    return currentVersion;
  }
}
