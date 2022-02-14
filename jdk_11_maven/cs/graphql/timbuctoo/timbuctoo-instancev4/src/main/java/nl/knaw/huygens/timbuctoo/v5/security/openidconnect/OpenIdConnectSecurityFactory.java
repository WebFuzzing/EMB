package nl.knaw.huygens.timbuctoo.v5.security.openidconnect;

import nl.knaw.huygens.timbuctoo.security.BasicPermissionFetcher;
import nl.knaw.huygens.timbuctoo.security.JsonBasedAuthorizer;
import nl.knaw.huygens.timbuctoo.security.JsonBasedUserStore;
import nl.knaw.huygens.timbuctoo.security.dataaccess.AccessFactory;
import nl.knaw.huygens.timbuctoo.util.Timeout;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.SecurityFactory;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AccessNotPossibleException;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OpenIdConnectSecurityFactory implements SecurityFactory {
  private final Timeout timeout;
  private final OpenIdClient openIdClient;
  private final AccessFactory accessFactory;

  OpenIdConnectSecurityFactory(Timeout timeout, AccessFactory accessFactory,
                               OpenIdClient openIdClient) {

    this.timeout = timeout;
    this.openIdClient = openIdClient;
    this.accessFactory = accessFactory;
  }

  @Override
  public Iterator<Tuple<String, Supplier<Optional<String>>>> getHealthChecks() {
    // TODO OpenID Connect file checks
    return accessFactory.getHealthChecks();
  }

  @Override
  public UserValidator getUserValidator() throws AccessNotPossibleException, NoSuchAlgorithmException {
    return new OpenIdConnectUserValidator(
        timeout,
        openIdClient,
        new JsonBasedUserStore(accessFactory.getUserAccess())
    );
  }

  @Override
  public PermissionFetcher getPermissionFetcher() throws AccessNotPossibleException, NoSuchAlgorithmException {
    return new BasicPermissionFetcher(
        new JsonBasedAuthorizer(accessFactory.getVreAuthorizationAccess()),
        accessFactory.getPermissionConfig()
    );
  }

  @Override
  public void register(Consumer<Object> registerToJersey) throws NoSuchAlgorithmException, AccessNotPossibleException {
    registerToJersey.accept(new LoginEndPoint(openIdClient));
  }
}
