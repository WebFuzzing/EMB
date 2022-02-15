package nl.knaw.huygens.timbuctoo.server;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

class PromiscuousCorsFilter implements ContainerResponseFilter {
  @Override
  public void filter(ContainerRequestContext containerRequestContext,
                     ContainerResponseContext response) throws IOException {
    response.getHeaders().add("Access-Control-Allow-Origin", "*");
    response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, vre_id");
    response.getHeaders().add("Access-Control-Allow-Credentials", "true");
    response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    response.getHeaders().add("Access-Control-Expose-Headers", "Location, Link, X_AUTH_TOKEN, VRE_ID");
  }
}
