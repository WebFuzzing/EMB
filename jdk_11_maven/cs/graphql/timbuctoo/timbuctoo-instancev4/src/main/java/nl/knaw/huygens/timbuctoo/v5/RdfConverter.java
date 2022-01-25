package nl.knaw.huygens.timbuctoo.v5;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RdfConverter {
  public static void main(String[] args) throws IOException {
    final Model model = Rio.parse(
        new FileReader("/home/martijnm/Desktop/emdates_places_opole.ttl"),
        "http://example.com",
        RDFFormat.TURTLE
    );

    Rio.write(model, new FileWriter("/home/martijnm/Desktop/emdates_places_opole.nq"), RDFFormat.NQUADS);
  }
}
