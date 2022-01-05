package nl.knaw.huygens.timbuctoo.v5.security.twitterexample;

import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.SecurityFactory;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AccessNotPossibleException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TwitterSecurityFactory implements SecurityFactory {

  private final TwitterUserValidator twitterUserValidator;

  public TwitterSecurityFactory() {
    twitterUserValidator = new TwitterUserValidator();
  }

  @Override
  public Iterator<Tuple<String, Supplier<Optional<String>>>> getHealthChecks() {
    return Collections.emptyIterator();
  }

  @Override
  public UserValidator getUserValidator() throws AccessNotPossibleException, NoSuchAlgorithmException {
    return twitterUserValidator;
  }

  @Override
  public PermissionFetcher getPermissionFetcher() throws AccessNotPossibleException, NoSuchAlgorithmException {
    return new PermissionFetcher() {
      @Override
      public Set<Permission> getPermissions(User user, DataSetMetaData dataSetMetaData)
        throws PermissionFetchingException {
        HashSet<Permission> result = new HashSet<>();
        if (user != null && user.equals(dataSetMetaData.getOwnerId())) {
          result.add(Permission.IMPORT_DATA);
          result.add(Permission.WRITE);
        } else {
          result.add(Permission.READ);
        }
        return result;
      }

      @Override
      public boolean hasPermission(User user, DataSetMetaData dataSet, Permission permission)
        throws PermissionFetchingException {
        return getPermissions(user, dataSet).contains(permission);
      }

      @Override
      public Set<Permission> getOldPermissions(User user, String vreId) throws PermissionFetchingException {
        HashSet<Permission> result = new HashSet<>();
        result.add(Permission.READ);
        return result;
      }

      @Override
      public void initializeOwnerAuthorization(User user, String ownerId,
                                               String dataSetId) throws PermissionFetchingException,
        AuthorizationCreationException {
      }

      @Override
      public void removeAuthorizations(String vreId) throws PermissionFetchingException {
      }
    };
  }

  @Override
  public void register(Consumer<Object> registerToJersey) throws NoSuchAlgorithmException, AccessNotPossibleException {
    registerToJersey.accept(new TwitterLogin());
  }
}
