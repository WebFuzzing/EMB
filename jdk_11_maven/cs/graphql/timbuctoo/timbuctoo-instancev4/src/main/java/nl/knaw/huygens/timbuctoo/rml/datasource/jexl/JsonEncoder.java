package nl.knaw.huygens.timbuctoo.rml.datasource.jexl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEncoder {
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String stringify(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }
}
