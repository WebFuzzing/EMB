package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class UriHelper {
  private URI baseUri;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public UriHelper(URI baseUri) {
    this.baseUri = baseUri;
  }

  public URI fromResourceUri(URI resourceUri) {
    URI baseUri = UriBuilder.fromUri(this.baseUri).build();
    return UriBuilder.fromUri(resourceUri)
                     .userInfo(baseUri.getUserInfo())
                     .scheme(baseUri.getScheme())
                     .host(baseUri.getHost())
                     .port(baseUri.getPort())
                     .replacePath(baseUri.getPath()).path(resourceUri.getPath())
                     .build();
  }

  public URI getBaseUri() {
    return baseUri;
  }

  public void notifyOfPort(int port) {
    if (baseUri.getPort() == 0) {
      baseUri = UriBuilder
        .fromUri(baseUri)
        .port(port)
        .build();
    }
  }
}
