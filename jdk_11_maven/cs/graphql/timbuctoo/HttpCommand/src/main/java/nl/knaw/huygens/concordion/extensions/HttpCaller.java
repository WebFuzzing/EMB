package nl.knaw.huygens.concordion.extensions;

public interface HttpCaller {
  ActualResult call(HttpRequest value);
}
