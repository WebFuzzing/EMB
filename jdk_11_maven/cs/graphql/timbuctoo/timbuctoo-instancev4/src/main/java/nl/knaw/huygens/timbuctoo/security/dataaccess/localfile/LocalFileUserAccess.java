package nl.knaw.huygens.timbuctoo.security.dataaccess.localfile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.knaw.huygens.timbuctoo.security.JsonBasedUserStore;
import nl.knaw.huygens.timbuctoo.security.dataaccess.UserAccess;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LocalFileUserAccess implements UserAccess {
  public final Path usersFile;
  public final ObjectMapper objectMapper;

  public LocalFileUserAccess(Path usersFile) {
    this.usersFile = usersFile;
    objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  @Override
  public void addUser(User user) throws AuthenticationUnavailableException {
    final List<User> users;
    try {
      synchronized (usersFile) {
        users = objectMapper.readValue(usersFile.toFile(), new TypeReference<List<User>>() {
        });
      }
      users.add(user);
      objectMapper.writeValue(usersFile.toFile(), users.toArray(new User[users.size()]));
    } catch (IOException e) {
      JsonBasedUserStore.LOG.error("Cannot read {}", usersFile.toAbsolutePath());
      JsonBasedUserStore.LOG.error("Exception thrown", e);
      throw new AuthenticationUnavailableException(e.getMessage());
    }
  }

  @Override
  public Optional<User> getUserForPid(String pid) throws AuthenticationUnavailableException {
    List<User> users = getUsers();

    return users.stream().filter(user -> Objects.equals(user.getPersistentId(), pid)).findFirst();
  }

  @Override
  public Optional<User> getUserForTimLocalId(String userId) throws AuthenticationUnavailableException {
    List<User> users = getUsers();

    return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
  }

  private List<User> getUsers() throws AuthenticationUnavailableException {
    List<User> users;
    try {
      synchronized (usersFile) {
        users = objectMapper.readValue(usersFile.toFile(), new TypeReference<List<User>>() {
        });
      }
    } catch (IOException e) {
      JsonBasedUserStore.LOG.error("Cannot read {}", usersFile.toAbsolutePath());
      JsonBasedUserStore.LOG.error("Exception thrown", e);
      throw new AuthenticationUnavailableException(e.getMessage());
    }
    return users;
  }
}
