package nl.knaw.huygens.security.client;

import com.google.common.collect.LinkedListMultimap;

import java.util.Optional;

public class ActualResultWithBody<T> extends ActualResult {
  private final Optional<T> body;

  public ActualResultWithBody(int status, String statusInfo, LinkedListMultimap<String, String> headers,
                              Optional<T> body) {
    super(status, statusInfo, headers);
    this.body = body;
  }

  public Optional<T> getBody() {
    return body;
  }
}
