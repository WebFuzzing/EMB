package nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class JsonFileBackedDataTest {
  @Test
  public void itStoresData() throws Exception {
    File tmpFile = Files.createTempFile("prefix", "asd").toFile();
    try {
      tmpFile.delete(); //the file must either not exist, or contain valid data
      JsonFileBackedData<Map<String, String>> instance = JsonFileBackedData.getOrCreate(
        tmpFile,
        () -> new HashMap<>(),
        new TypeReference<Map<String, String>>() {
        }
      );

      assertThat(instance.getData().isEmpty(), is(true));
      assertThat(new String(Files.readAllBytes(tmpFile.toPath()), Charsets.UTF_8), is("{ }"));

      instance.updateData(data -> {
        data.put("foo", "utf-8 ☃");
        return data;
      });

      assertThat(new String(Files.readAllBytes(tmpFile.toPath()), Charsets.UTF_8), is("{\n  \"foo\" : \"utf-8 ☃\"\n}"));

      JsonFileBackedData.regenerateSoWeCanTestHowWellLoadingWorks(tmpFile);

      JsonFileBackedData<Map<String, String>> reloadedInstance = JsonFileBackedData.getOrCreate(
        tmpFile,
        () -> new HashMap<>(),
        new TypeReference<Map<String, String>>() {
        }
      );

      assertThat(reloadedInstance.getData().get("foo"), is("utf-8 ☃"));
    } finally {
      tmpFile.delete();
    }
  }

  @Test
  public void itCanBeInitializedToNull() throws Exception {
    File tmpFile = Files.createTempFile("prefix", "asd").toFile();
    try {
      tmpFile.delete(); //the file must either not exist, or contain valid data
      JsonFileBackedData<Map<String, String>> instance = JsonFileBackedData.getOrCreate(
        tmpFile,
        () -> null,
        new TypeReference<Map<String, String>>() {
        }
      );
      assertThat(instance.getData(), is(nullValue()));
      assertThat(new String(Files.readAllBytes(tmpFile.toPath()), Charsets.UTF_8), is("null"));
    } finally {
      tmpFile.delete(); //the file must either not exist, or contain valid data
    }
  }


}
