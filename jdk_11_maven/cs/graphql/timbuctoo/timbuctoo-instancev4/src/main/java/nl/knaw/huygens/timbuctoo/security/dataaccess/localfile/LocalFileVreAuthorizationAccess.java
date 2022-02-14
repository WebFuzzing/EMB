package nl.knaw.huygens.timbuctoo.security.dataaccess.localfile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.security.dataaccess.VreAuthorizationAccess;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LocalFileVreAuthorizationAccess implements VreAuthorizationAccess {
  public static final Logger LOG = LoggerFactory.getLogger(LocalFileVreAuthorizationAccess.class);
  private final ObjectMapper objectMapper;
  private final Path authorizationsFolder;

  public LocalFileVreAuthorizationAccess(Path authorizationsFolder) {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new GuavaModule());
    this.authorizationsFolder = authorizationsFolder;
  }

  @Override
  public VreAuthorization getOrCreateAuthorization(String vreId, String userId, String userRole)
    throws AuthorizationUnavailableException {
    Optional<VreAuthorization> authOptional = getAuthorization(vreId, userId);

    if (authOptional.isPresent()) {
      return authOptional.get();
    } else {
      try {
        synchronized (authorizationsFolder) {
          File file = getFileOfVre(vreId);
          List<VreAuthorization> authorizations = Lists.newArrayList();
          if (file.exists()) {
            authorizations =
              objectMapper.readValue(file, new TypeReference<List<VreAuthorization>>() {
              });
          }
          VreAuthorization vreAuthorization = VreAuthorization.create(vreId, userId, userRole);
          authorizations.add(vreAuthorization);

          objectMapper.writeValue(file, authorizations.toArray(new VreAuthorization[authorizations.size()]));
          return vreAuthorization;
        }
      } catch (IOException e) {
        LOG.error("Error creating VRE Authorization", e);
        throw new AuthorizationUnavailableException(e.getMessage());
      }
    }
  }


  @Override
  public Optional<VreAuthorization> getAuthorization(String vreId, String userId)
    throws AuthorizationUnavailableException {

    Optional<VreAuthorization> authorizationValue = Optional.empty();

    File file = getFileOfVre(vreId);

    authorizationValue = getAuthorization(userId, authorizationValue, file);

    if (!authorizationValue.isPresent()) {
      file = authorizationsFolder.resolve("authorizations.json").toFile();

      authorizationValue = getAuthorization(userId, authorizationValue, file);

    }

    return authorizationValue;
  }

  private Optional<VreAuthorization> getAuthorization(String userId, Optional<VreAuthorization> authorizationValue,
                                                      File file) throws AuthorizationUnavailableException {
    if (file.exists()) {
      try {
        List<VreAuthorization> authorizations;
        synchronized (authorizationsFolder) {
          authorizations =
            objectMapper.readValue(file, new TypeReference<List<VreAuthorization>>() {
            });
        }
        authorizationValue = authorizations.stream()
                                           .filter(authorization -> Objects.equals(authorization.getUserId(), userId))
                                           .findAny();
      } catch (IOException e) {
        LOG.error("Error reading VRE Authorizations file '{}' ", file.getAbsolutePath());
        LOG.error("Could not read VRE Authorizations file", e);
        throw new AuthorizationUnavailableException(e.getMessage());
      }
    }
    return authorizationValue;
  }

  @Override
  public void deleteVreAuthorizations(String vreId) throws AuthorizationUnavailableException {
    synchronized (authorizationsFolder) {
      if (!getFileOfVre(vreId).delete()) {
        throw new AuthorizationUnavailableException("Failed to delete vre authorizations for vre '" + vreId + "'");
      }
    }
  }



  private File getFileOfVre(String vreId) {

    File directory;
    /*
     * The authorizations of the specific data set are placed in the folder of the data set.
     * For legacy data sets this means {dataSetsPath}/{dataSetName}.
     * For new data sets this will be {dataSetsPath}/{ownerId}/{dataSetName}.
     */
    if (vreId.contains("__")) { // new style vre
      Tuple<String, String> ownerIdDataSetId = DataSetMetaData.splitCombinedId(vreId);
      directory = authorizationsFolder.resolve(ownerIdDataSetId.getLeft())
                                      .resolve(ownerIdDataSetId.getRight().replace("__", ""))
                                      .toFile();
    } else {
      // old style vre
      directory = authorizationsFolder.resolve(vreId).toFile();
    }

    directory.mkdirs();
    return new File(directory, "authorizations.json");
  }

}
