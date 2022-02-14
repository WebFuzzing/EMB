package nl.knaw.huygens.timbuctoo.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import nl.knaw.huygens.timbuctoo.security.dto.typeidresolvers.LoginTypeIdResolver;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonTypeIdResolver(LoginTypeIdResolver.class) // be able to map the login java type and the serialized version
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Immutable
@JsonSerialize(as = ImmutableLogin.class)
@JsonDeserialize(as = ImmutableLogin.class)
public interface Login {

  static Login create(String username, String userPid, byte[] password, byte[] salt, String givenName, String surname,
                      String emailAddress, String organization) {
    return ImmutableLogin.builder()
      .username(username)
      .userPid(userPid)
      .password(password)
      .salt(salt)
      .givenName(givenName)
      .surName(surname)
      .emailAddress(emailAddress)
      .organization(organization)
      .build();
  }

  String getUserPid();

  byte[] getPassword();

  byte[] getSalt();

  @JsonProperty("userName")
  String getUsername();

  @Nullable
  String getGivenName();

  @Nullable
  String getSurName();

  @Nullable
  String getEmailAddress();

  @Nullable
  String getOrganization();
}
