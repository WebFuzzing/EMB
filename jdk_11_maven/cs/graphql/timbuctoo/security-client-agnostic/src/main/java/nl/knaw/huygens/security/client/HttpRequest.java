package nl.knaw.huygens.security.client;

import com.google.common.base.Splitter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpRequest {
  public final String method;
  private String path;
  public final LinkedListMultimap<String, String> headers;
  public final String body;
  public final String server;
  public final LinkedListMultimap<String, String> queryParameters;

  public HttpRequest(
    String method,
    String url,
    LinkedListMultimap<String, String> headers,
    String body,
    String server,
    LinkedListMultimap<String, String> queryParameters
  ) {
    if (url != null) {
      URI uri = URI.create(url);
      if (uri.isOpaque()) {
        throw new IllegalArgumentException("Should be a URL, not a URI");
      }
      this.path = uri.getPath();
      if (uri.isAbsolute() && server == null) {
        this.server = uri.getScheme() + "://" + uri.getRawAuthority();
      } else {
        this.server = server;
      }
      //if you have query parameters in the base url _and_ separate query parameters then the result is a concatenation
      if (uri.getRawQuery() != null) {
        this.queryParameters = LinkedListMultimap.create();
        for (String querySegment : Splitter.on("&").split(uri.getRawQuery())) {
          int equalsLocation = querySegment.indexOf('=');
          String key = querySegment.substring(0, equalsLocation);
          String value = querySegment.substring(equalsLocation + 1);
          this.queryParameters.put(key, value);
        }
        if (queryParameters != null) {
          this.queryParameters.putAll(queryParameters);
        }
      } else {
        this.queryParameters = queryParameters;
      }
    } else {
      this.server = server;
      this.path = "";
      this.queryParameters = queryParameters;
    }
    this.method = method;
    this.headers = headers;
    this.body = body;
  }

  public HttpRequest(String method, String url) {
    this(method, url, LinkedListMultimap.create(), null, null, LinkedListMultimap.create());
  }

  public HttpRequest(String method, String url, String body) {
    this(method, url, LinkedListMultimap.create(), body, null, LinkedListMultimap.create());
  }

  public HttpRequest withHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public HttpRequest withExtraPath(String path) {
    if (path == null || path.equals("")) {
      return this;
    }
    if (!this.path.endsWith("/")) {
      this.path += "/";
    }
    if (path.charAt(0) == '/') {
      path = path.substring(1);
    }
    this.path += path;
    return this;
  }

  public HttpRequest withHeaders(List<? extends Map.Entry<String, String>> headers) {
    headers.forEach(entry -> this.headers.put(entry.getKey(), entry.getValue()));
    return this;
  }

  public HttpRequest withQueryParam(String key, String value) {
    queryParameters.put(key, value);
    return this;
  }

  public HttpRequest withQueryParams(List<? extends Map.Entry<String, String>> params) {
    params.forEach(entry -> this.queryParameters.put(entry.getKey(), entry.getValue()));
    return this;
  }

  public String getPath() {
    return this.path;
  }

  public String getPathAndQuery() {
    String url = this.path;
    boolean isFirst = true;
    for (Map.Entry<String, String> queryParameter : this.queryParameters.entries()) {
      if (isFirst) {
        url += "?";
        isFirst = false;
      } else {
        url += "&";
      }
      Escaper escaper = UrlEscapers.urlFormParameterEscaper();
      url += escaper.escape(queryParameter.getKey()) + "=" + escaper.escape(queryParameter.getValue());
    }

    return url;
  }

  public String getUrl() {
    String path = getPathAndQuery();
    if (path.charAt(0) == '/') {
      return server + path;
    } else {
      return server + "/" + path;
    }
  }

}
