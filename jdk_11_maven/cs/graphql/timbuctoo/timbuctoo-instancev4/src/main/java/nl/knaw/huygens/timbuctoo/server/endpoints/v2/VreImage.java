package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2.1/system/vre/{vreName}/image/{rev}")
public class VreImage {

  private TransactionEnforcer transactionEnforcer;

  public VreImage(TransactionEnforcer transactionEnforcer) {

    this.transactionEnforcer = transactionEnforcer;
  }

  @GET
  public Response getImage(@PathParam("vreName") String vreName) {

    return transactionEnforcer.executeAndReturn(timbuctooActions -> {
      final Vre vre = timbuctooActions.getVre(vreName);
      if (vre == null) {
        return TransactionStateAndResult.commitAndReturn(Response.status(Response.Status.NOT_FOUND).build());
      }

      final byte[] imageBlob = timbuctooActions.getVreImageBlob(vreName);
      final MediaType mediaType = vre.getMetadata().getImageMediaType();

      if (imageBlob != null && mediaType != null) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(604800);
        cacheControl.setPrivate(false);
        return TransactionStateAndResult.commitAndReturn(Response
          .ok(imageBlob).type(mediaType).cacheControl(cacheControl).build());
      } else {
        return TransactionStateAndResult.commitAndReturn(Response.status(Response.Status.NOT_FOUND).build());
      }
    });
  }
}
