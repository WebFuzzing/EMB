package nl.knaw.huygens.timbuctoo.v5.dataset;

public interface DataProvider {
  void subscribeToRdf(RdfProcessor processor);
}
