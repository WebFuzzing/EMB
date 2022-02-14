package nl.knaw.huygens.concordion.extensions;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;

public class ExpectedResult {
  private final int status;
  private final String body;
  private final LinkedListMultimap<String, String> headers;

  public ExpectedResult(int status, String body, LinkedListMultimap<String, String> headers) {
    this.status = status;
    this.body = body;
    this.headers = headers;
  }

  public boolean hasBody() {
    return !Strings.isNullOrEmpty(body);
  }

  public int getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

  public LinkedListMultimap<String, String> getHeaders() {
    return headers;
  }

  public static class ExpectedResultBuilder {
    private int status;
    private String body;
    private final LinkedListMultimap<String, String> headers;

    public ExpectedResultBuilder() {
      headers = LinkedListMultimap.create();
    }

    public static ExpectedResultBuilder expectedResult() {
      return new ExpectedResultBuilder();
    }

    public ExpectedResultBuilder withHeader(String key, String value) {
      headers.put(key.toLowerCase(), value);
      return this;
    }

    public ExpectedResultBuilder withStatus(int status) {
      this.status = status;
      return this;
    }

    public ExpectedResultBuilder withBody(String body) {
      this.body = body;
      return this;
    }

    public ExpectedResult build() {
      return new ExpectedResult(status, body, headers);
    }
  }
}
