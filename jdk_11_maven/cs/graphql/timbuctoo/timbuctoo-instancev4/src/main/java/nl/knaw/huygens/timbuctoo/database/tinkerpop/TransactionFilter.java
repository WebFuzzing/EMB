package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
@Priority(Integer.MIN_VALUE + 1)
public class TransactionFilter implements ContainerRequestFilter, ContainerResponseFilter {

  public static final Logger LOG = LoggerFactory.getLogger(TransactionFilter.class);

  private final TinkerPopGraphManager graphWrapper;

  public TransactionFilter(TinkerPopGraphManager graphWrapper) {
    this.graphWrapper = graphWrapper;
  }


  @Override
  public void filter(final ContainerRequestContext context) {
    if (graphWrapper.getGraph().tx().isOpen()) {
      LOG.error("Leftover transaction");
    }
  }

  @Override
  public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
    if (graphWrapper.getGraph().tx().isOpen()) {
      if (isSafeMethod(requestContext.getMethod())) {
        LOG.error("Dangling transaction, you might have lost data! (closing it, causing a rollback)");
      }
      graphWrapper.getGraph().tx().close();
    }
  }

  private boolean isSafeMethod(String method) {
    return method.equals("GET") || method.equals("OPTIONS") || method.equals("HEAD");
  }

}
