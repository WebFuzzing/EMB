package nl.knaw.huygens.timbuctoo.rdf;

public class RdfProperties {
  public static final String RDF_URI_PROP = "rdfUri";
  public static final String RDFINDEX_NAME = "rdfUrls";
  public static final String RDF_SYNONYM_PROP = "rdfAlternatives";

  private RdfProperties() {
    throw new RuntimeException("Class should not be instantiated");
  }
}
