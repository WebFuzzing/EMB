package nl.knaw.huygens.timbuctoo.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedListMultimap;
import nl.knaw.huygens.security.client.ActualResult;
import nl.knaw.huygens.security.client.ActualResultWithBody;
import nl.knaw.huygens.security.client.HttpRequest;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class HttpCaller implements nl.knaw.huygens.security.client.HttpCaller {

  private static final Logger LOG = LoggerFactory.getLogger(HttpCaller.class);
  private final HttpClient client;
  private final ObjectMapper mapper;

  public HttpCaller(HttpClient httpClient) {
    this.client = httpClient;
    this.mapper = new ObjectMapper();
  }

  @Override
  public ActualResult call(HttpRequest value) throws IOException {
    return executeRequest(value, response -> {
      LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
      for (Header header : response.getAllHeaders()) {
        headers.put(header.getName(), header.getValue());
      }

      return new ActualResult(
        response.getStatusLine().getStatusCode(),
        response.getStatusLine().getReasonPhrase(),
        headers
      );
    });
  }

  @Override
  public <T> ActualResultWithBody<T> call(HttpRequest value, Class<? extends T> clazz) throws IOException {
    return executeRequest(value, response -> {
      T body;
      StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() == 200) {
        InputStream content = response.getEntity().getContent();
        body = mapper.readValue(content, clazz);
      } else {
        LOG.warn("Repsonse was '{}' with reason '{}'", statusLine.getStatusCode(), statusLine.getReasonPhrase());
        body = null;
      }

      LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
      for (Header header : response.getAllHeaders()) {
        headers.put(header.getName(), header.getValue());
      }

      return new ActualResultWithBody<>(
        statusLine.getStatusCode(),
        statusLine.getReasonPhrase(),
        headers,
        Optional.ofNullable(body)
      );
    });
  }

  private <T> T executeRequest(HttpRequest value, ValueTransformer<T> valueConverter) throws IOException {
    HttpRequestBase request = createRequest(value);

    try {
      HttpResponse response = client.execute(request);
      T returnValue = valueConverter.transform(response);
      return returnValue;
    } finally {
      request.reset();
    }
  }

  private HttpRequestBase createRequest(HttpRequest value) {
    HttpRequestBase request;

    switch (value.method) {
      case "POST":
        request = new HttpPost(value.getUrl());
        break;
      case "GET":
        request = new HttpGet(value.getUrl());
        break;
      case "PUT":
        request = new HttpPut(value.getUrl());
        break;
      case "DELETE":
        request = new HttpDelete(value.getUrl());
        break;
      case "OPTIONS":
        request = new HttpOptions(value.getUrl());
        break;
      case "HEAD":
        request = new HttpHead(value.getUrl());
        break;
      case "TRACE":
        request = new HttpTrace(value.getUrl());
        break;
      default:
        throw new IllegalArgumentException("HTTP method not supported");
    }

    for (Map.Entry<String, String> header : value.headers.entries()) {
      request.setHeader(header.getKey(), header.getValue());
    }
    return request;
  }

  private interface ValueTransformer<T> {
    T transform(HttpResponse response) throws IOException;
  }

}
