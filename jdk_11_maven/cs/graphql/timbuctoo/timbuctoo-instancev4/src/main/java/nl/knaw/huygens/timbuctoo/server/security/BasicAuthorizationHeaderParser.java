package nl.knaw.huygens.timbuctoo.server.security;

import java.util.Base64;

public class BasicAuthorizationHeaderParser {

  /**
   * Parses the string Basic [some base64 hash] into a username and password.
   *
   * @throws InvalidAuthorizationHeaderException When the header does not start with 'Basic ',
   *                                             when the hash is not valid Base64 encoded,
   *                                             or when there is no ':' in the decoded string.
   */
  public static Credentials parse(String authorizationHeader) throws InvalidAuthorizationHeaderException {
    if (!authorizationHeader.toLowerCase().startsWith("basic ")) {
      throw new InvalidAuthorizationHeaderException("Header must start with the word 'Basic' followed by 1 space.");
    }
    int indexOfWhitespace = authorizationHeader.indexOf(" ");
    String rawAuthString = authorizationHeader.substring(indexOfWhitespace + 1);

    String decodedAuth = null;
    try {
      decodedAuth = new String(Base64.getDecoder().decode(rawAuthString.getBytes()));
    } catch (IllegalArgumentException e) {
      throw new InvalidAuthorizationHeaderException(e.getMessage());
    }

    if (!decodedAuth.contains(":")) {
      throw new InvalidAuthorizationHeaderException(String.format(
        "The username and password should be seperated by a ':' but that character was not found in '%s'.",
        decodedAuth));
    }

    int indexOfFirstColon = decodedAuth.indexOf(":");
    String username = decodedAuth.substring(0, indexOfFirstColon);
    String password = decodedAuth.substring(indexOfFirstColon + 1);

    return new Credentials(username, password);
  }

  public static class Credentials {
    private final String username;
    private final String password;

    public Credentials(String username, String password) {

      this.username = username;
      this.password = password;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }
  }
}
