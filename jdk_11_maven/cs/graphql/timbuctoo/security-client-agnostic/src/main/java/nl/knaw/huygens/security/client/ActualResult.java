package nl.knaw.huygens.security.client;

import com.google.common.collect.LinkedListMultimap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ActualResult {
  private final int status;
  private final String statusInfo;
  private final LinkedListMultimap<String, String> headers;

  public ActualResult(int status, String statusInfo, LinkedListMultimap<String, String> headers) {
    this.status = status;
    this.statusInfo = statusInfo;
    this.headers = headers;
  }

  public int getStatus() {
    return status;
  }

  /**
   * The header collection.
   * @return headers, keys always lowercase
   */
  public LinkedListMultimap<String, String> getMultiHeaders() {
    return headers;
  }

  /**
   * Use this if you expect only one header and are in a handlebars org OGNL context.
   * <p />
   * In those contexts this method allows you to write <pre>result.firstHeaders.location</pre> instead of
   * <pre>result.getFirstHeader("location")</pre>
   * @return a map with the first header of the collection indexed by the lowercased fieldname
   */
  public Map<String, String> getFirstHeaders() {
    HashMap<String, String> result = new HashMap<>();
    for (String key : headers.keySet()) {
      result.put(key.toLowerCase(), headers.get(key).get(0));
    }
    return result;
  }

  /**
   * Use this when you need a specific header, and you're not interested in duplucates.
   * <p/>
   * Use <pre>getMultiHeaders.get(fieldName)</pre> if you expect multiple headers
   * @param fieldName the header name
   * @return the first header or Optional.empty if no header was defined
   */
  public Optional<String> getFirstHeader(String fieldName) {
    Iterator<String> headerValues = headers.get(fieldName.toLowerCase()).iterator();
    if (headerValues.hasNext()) {
      return Optional.of(headerValues.next());
    } else {
      return Optional.empty();
    }
  }
  public String getStatusInfo() {
    return statusInfo;
  }
}
