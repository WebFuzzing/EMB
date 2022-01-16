package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.LoginAccess;
import nl.knaw.huygens.timbuctoo.security.dto.Login;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

public class JsonBasedAuthenticator implements Authenticator, LoginCreator {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  public static final Logger LOG = LoggerFactory.getLogger(JsonBasedAuthenticator.class);
  private final String algorithm;
  private final LoginAccess loginAccess;

  public JsonBasedAuthenticator(LoginAccess loginAccess, String encryptionAlgorithm) throws NoSuchAlgorithmException {
    MessageDigest.getInstance(encryptionAlgorithm);
    this.loginAccess = loginAccess;
    this.algorithm = encryptionAlgorithm;
  }

  @Override
  public Optional<String> authenticate(String username, String password) throws LocalLoginUnavailableException {
    try {
      Optional<Login> first = loginAccess.getLogin(username);

      if (first.isPresent()) {
        Login login = first.get();

        return isCorrectPassword(password, login) ? Optional.of(login.getUserPid()) : Optional.empty();
      }
    } catch (NoSuchAlgorithmException e) {
      LOG.error("Encryption algorithm can not be found.", e);
      throw new LocalLoginUnavailableException(e.getMessage());
    }
    return Optional.empty();
  }

  private boolean isCorrectPassword(String password, Login login) throws NoSuchAlgorithmException {
    byte[] encryptedPassword = encryptPassword(password, login.getSalt());

    return new String(encryptedPassword, UTF8).equals(new String(login.getPassword(), UTF8));
  }

  // inspired by https://www.owasp.org/index.php/Hashing_Java
  private byte[] encryptPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    messageDigest.reset();
    messageDigest.update(salt);
    return messageDigest.digest(password.getBytes());
  }

  @Override
  public void createLogin(String userPid, String userName, String password, String givenName, String surname,
                          String email, String organization) throws LoginCreationException {

    try {
      Login login = create(userPid, userName, password, givenName, surname, email, organization);
      loginAccess.addLogin(login);
    } catch (NoSuchAlgorithmException e) {
      LOG.error("Encryption algorithm can not be found.", e);
      throw new LoginCreationException(e.getMessage());
    }
  }

  public Login create(String userPid, String userName, String password, String givenName, String surname, String email,
                      String organization) throws NoSuchAlgorithmException {
    byte[] salt = createSalt();
    byte[] encryptedPassword = encryptPassword(password, salt);

    return Login.create(userName, userPid, encryptedPassword, salt, givenName, surname, email, organization);
  }

  private byte[] createSalt() {
    return UUID.randomUUID().toString().getBytes();
  }
}
