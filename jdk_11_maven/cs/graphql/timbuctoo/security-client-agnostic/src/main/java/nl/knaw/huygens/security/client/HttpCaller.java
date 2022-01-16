package nl.knaw.huygens.security.client;

import java.io.IOException;

public interface HttpCaller {
  ActualResult call(HttpRequest value) throws IOException;

  <T> ActualResultWithBody<T> call(HttpRequest value, Class<? extends T> clazz) throws IOException;
}
