package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class TypeNames {
  @JsonProperty
  public Map<String, String> prefixes = new HashMap<>();

  @JsonProperty
  public Map<String, String> shorteneds = new HashMap<>();

  @JsonProperty
  public Map<String, String> inverse = new HashMap<>();
}
