package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.auth;

import io.vavr.control.Either;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.IllegalDataSetNameException;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

public class AuthCheck {
  private static final Logger LOG = LoggerFactory.getLogger(AuthCheck.class);
  private final UserValidator userValidator;
  private final PermissionFetcher permissionFetcher;
  private final DataSetRepository dataSetRepository;

  public AuthCheck(UserValidator userValidator, PermissionFetcher permissionFetcher,
                   DataSetRepository dataSetRepository) {
    this.userValidator = userValidator;
    this.permissionFetcher = permissionFetcher;
    this.dataSetRepository = dataSetRepository;
  }

  private static Either<Response, User> getUser(String authHeader, UserValidator userValidator) {
    try {
      return userValidator.getUserFromAccessToken(authHeader)
        .map(Either::<Response, User>right)
        .orElseGet(() -> Either.left(Response.status(Response.Status.UNAUTHORIZED).build()));
    } catch (UserValidationException e) {
      return Either.left(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }

  private Either<Response, Tuple<User, DataSet>> handleForceCreate(String ownerId, String dataSetId,
                                                                  boolean forceCreation, User user) {
    if (forceCreation) {
      if (dataSetRepository.userMatchesPrefix(user, ownerId)) {
        try {
          final DataSet dataSet = dataSetRepository.createDataSet(user, dataSetId);
          return Either.right(Tuple.tuple(user, dataSet));
        } catch (DataStoreCreationException | DataSetCreationException e) {
          LOG.error(e.getMessage());
          return Either.left(Response.serverError().build());
        } catch (IllegalDataSetNameException e) {
          return Either.left(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
        }
      }
    }
    return Either.left(Response.status(Response.Status.FORBIDDEN).build());
  }

  public Either<Response, Tuple<User, DataSet>> getOrCreate(String authHeader, String ownerId, String dataSetId,
                                                            boolean forceCreation) {
    return getUser(authHeader, userValidator)
      .flatMap(user ->
        dataSetRepository.getDataSet(user, ownerId, dataSetId)
          .map(ds -> Either.<Response, Tuple<User, DataSet>>right(Tuple.tuple(user, ds)))
          .orElseGet(() -> handleForceCreate(ownerId, dataSetId, forceCreation, user))
      );
  }

  public Either<Response, Tuple<User, DataSet>> allowedToImport(User user, DataSet dataSet) {
    try {
      if (permissionFetcher.hasPermission(user,dataSet.getMetadata(), Permission.IMPORT_DATA)) {
        return Either.right(Tuple.tuple(user, dataSet));
      } else {
        return Either.left(Response.status(Response.Status.FORBIDDEN).build());
      }
    } catch (PermissionFetchingException e) {
      return Either.left(Response.serverError().build());
    }
  }
}
