package nl.knaw.huygens.timbuctoo.v5.redirectionservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;

public class RedirectionServiceParameters {
  private final int retries;
  private final URI urlToRedirectTo;
  private final EntityLookup entityLookup;


  public RedirectionServiceParameters(URI urlToRedirectTo, EntityLookup entityLookup) {
    this.urlToRedirectTo = urlToRedirectTo;
    this.entityLookup = entityLookup;
    this.retries = 0;
  }

  @JsonCreator
  public RedirectionServiceParameters(
    @JsonProperty("urlToRedirectTo") URI urlToRedirectTo,
    @JsonProperty("entityLookup") EntityLookup entityLookup,
    @JsonProperty("retries") int retries
  ) {
    this.retries = retries;
    this.urlToRedirectTo = urlToRedirectTo;
    this.entityLookup = entityLookup;
  }

  public int getRetries() {
    return retries;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public URI getUrlToRedirectTo() {
    return urlToRedirectTo;
  }

  public EntityLookup getEntityLookup() {
    return entityLookup;
  }

  public RedirectionServiceParameters nextTry() {
    return new RedirectionServiceParameters(urlToRedirectTo, entityLookup, retries + 1);
  }
}
