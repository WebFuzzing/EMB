package nl.knaw.huygens.timbuctoo.rdfconversion;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RdfConverter {
  public static void main(String[] args) throws Exception {
    InputStream resourceAsStream = new FileInputStream(args[0]);
    Model model = Rio.parse(resourceAsStream, "", RDFFormat.NQUADS);
    Rio.write(model, new FileOutputStream(args[1]), RDFFormat.TURTLE);
  }
}
