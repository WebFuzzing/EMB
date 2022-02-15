package nl.knaw.huygens.timbuctoo.server.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.security.client.ActualResultWithBody;
import nl.knaw.huygens.security.client.HttpRequest;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpCallerTest {

  @Test
  public void callReturnsAnEmptyBodyWhenTheStatusCodeIsNotOk() throws IOException {
    HttpResponse invalidResponse = response(500, "invalidType");
    HttpClient httpClient = mock(HttpClient.class);
    when(httpClient.execute(any())).thenReturn(invalidResponse);
    HttpCaller instance = new HttpCaller(httpClient);

    ActualResultWithBody<TestClass> call = instance.call(new HttpRequest("GET", "http://example.org/"), TestClass.class);

    assertThat(call.getBody().isPresent(), is(false));
  }


  @Test
  public void callReturnsABodyWhenTheStatusCodeIsOk() throws IOException {
    TestClass testClass = new TestClass();
    testClass.test = "value";
    HttpResponse invalidResponse = response(200, new ObjectMapper().writeValueAsString(testClass));
    HttpClient httpClient = mock(HttpClient.class);
    when(httpClient.execute(any())).thenReturn(invalidResponse);
    HttpCaller instance = new HttpCaller(httpClient);

    ActualResultWithBody<TestClass> call = instance.call(new HttpRequest("GET", "http://example.org/"), TestClass.class);

    assertThat(call.getBody().isPresent(), is(true));
    assertThat(call.getBody().get().test, is("value"));
  }

  private HttpResponse response(final int status, String value) throws IOException {
    HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.getStatusLine()).thenReturn(new StatusLine() {
      @Override
      public ProtocolVersion getProtocolVersion() {
        return new ProtocolVersion("http", 1, 1);
      }

      @Override
      public int getStatusCode() {
        return status;
      }

      @Override
      public String getReasonPhrase() {
        return "";
      }
    });
    when(httpResponse.getAllHeaders()).thenReturn(new Header[]{});
    HttpEntity entity = mock(HttpEntity.class);
    when(entity.getContent()).thenReturn(new ByteArrayInputStream(value.getBytes()));
    when(httpResponse.getEntity()).thenReturn(entity);
    return httpResponse;
  }

  public static class TestClass {
    @JsonProperty("test")
    public String test;
  }
}


