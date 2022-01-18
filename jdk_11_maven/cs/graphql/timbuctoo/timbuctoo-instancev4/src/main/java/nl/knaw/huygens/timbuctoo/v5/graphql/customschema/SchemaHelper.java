package nl.knaw.huygens.timbuctoo.v5.graphql.customschema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;
import nl.knaw.huygens.timbuctoo.v5.jacksonserializers.TimbuctooCustomSerializers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaHelper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new GuavaModule())
    .registerModule(new TimbuctooCustomSerializers())
    .enable(SerializationFeature.INDENT_OUTPUT);

  private SchemaHelper() {

  }

  public static Map<String, List<ExplicitField>> readExistingSchema(File schemaFile) {
    Map<String, List<ExplicitField>> customSchema = new HashMap<>();

    if (schemaFile.exists()) {
      try {
        customSchema = OBJECT_MAPPER.readValue(schemaFile,
          new TypeReference<Map<String, List<ExplicitField>>>() {
          });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return customSchema;
  }


  public static void saveSchema(Map<String, List<ExplicitField>> schema, File schemaFile)
    throws IOException {
    OBJECT_MAPPER.writeValue(schemaFile, schema);
  }
}
