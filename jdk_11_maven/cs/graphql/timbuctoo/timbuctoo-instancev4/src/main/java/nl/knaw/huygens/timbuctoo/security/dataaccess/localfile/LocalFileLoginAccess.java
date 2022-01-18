package nl.knaw.huygens.timbuctoo.security.dataaccess.localfile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.security.dataaccess.LoginAccess;
import nl.knaw.huygens.timbuctoo.security.dto.Login;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class LocalFileLoginAccess implements LoginAccess {
  private static final Logger LOG = getLogger(LocalFileLoginAccess.class);
  private final Path loginsFile;
  private final ObjectMapper objectMapper;

  public LocalFileLoginAccess(Path loginsFile) {
    this.loginsFile = loginsFile;
    objectMapper = new ObjectMapper();
  }

  @Override
  public Optional<Login> getLogin(String username) throws LocalLoginUnavailableException {
    List<Login> logins;
    synchronized (loginsFile) {
      try {
        logins = objectMapper.readValue(loginsFile.toFile(), new TypeReference<List<Login>>() {});
      } catch (IOException e) {
        LOG.error("Could not read \"{}\"", loginsFile.toAbsolutePath());
        LOG.error("Exception", e);
        throw new LocalLoginUnavailableException(e.getMessage());
      }
    }
    return logins.stream().filter(login -> login.getUsername().equals(username)).findFirst();
  }

  @Override
  public void addLogin(Login login) throws LoginCreationException {
    synchronized (loginsFile) {
      try {
        List<Login> logins = objectMapper.readValue(loginsFile.toFile(), new TypeReference<List<Login>>() {
        });

        if (containsLoginForUserName(logins, login.getUsername())) {
          LOG.warn("Already contains a login for userName '{}'. No login added.", login.getUsername());
          return;
        }

        if (logins.stream().anyMatch(itemLogin -> Objects.equals(itemLogin.getUserPid(), login.getUserPid()))) {
          LOG.warn("Already contains a login for userPid '{}'. No login added", login.getUserPid());
          return;
        }

        logins.add(login);

        objectMapper.writeValue(loginsFile.toFile(), logins.toArray(new Login[logins.size()]));
      } catch (IOException e) {
        LOG.error("Could not read \"{}\"", loginsFile.toAbsolutePath());
        LOG.error("Exception", e);
        throw new LoginCreationException(e.getMessage());
      }
    }
  }

  private boolean containsLoginForUserName(List<Login> logins, String userName) {
    return logins.stream().anyMatch(login -> Objects.equals(login.getUsername(), userName));
  }
}
