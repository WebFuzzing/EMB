package nl.knaw.huygens.contractdiff.jsondiff;

import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import org.junit.Test;

import static nl.knaw.huygens.contractdiff.JsonBuilder.jsn;
import static nl.knaw.huygens.contractdiff.JsonBuilder.jsnA;
import static nl.knaw.huygens.contractdiff.JsonBuilder.jsnO;
import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JsonDifferTest {

  @Test
  public void handlesEqualValues() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "match", jsn(1)
      ),
      jsnO(
        "match", jsn(1)
      )
    );
    assertThat(result.wasSuccess(), is(true));
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
      "  \"match\": 1, //is 1\n" +
      "}\n")
    );

  }

  @Test
  public void handlesUnEqualValues() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "mismatch", jsn(1)
      ),
      jsnO(
        "mismatch", jsn(3)
      )
    );
    assertThat(result.wasSuccess(), is(false));
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"mismatch\": 1, //expected 3\n" +
        "}\n")
    );
  }

  @Test
  public void handlesSuperfluousValues() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "extra", jsn(2)
      ),
      jsnO(
      )
    );
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"extra\": 2, //not part of contract\n" +
        "}\n")
    );
  }


  @Test
  public void handlesMissingValues() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
      ),
      jsnO(
        "missing", jsn(2)
      )
    );
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"missing\": 2, //missing\n" +
        "}\n")
    );
  }

  @Test
  public void handlesDynamicMatchers() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "dynamicFail", jsn(1),
        "dynamicSuccess", jsn("1")
      ),
      jsnO(
        "dynamicFail", jsn("/*STRING*/"),
        "dynamicSuccess", jsn("/*STRING*/")
      )
    );
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"dynamicSuccess\": \"1\", //is a string\n" +
        "  \"dynamicFail\": 1, //expected a string\n" +
        "}\n")
    );
  }

  @Test
  public void handlesDynamicMatchersWithConfig() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "some_array", jsnA(
          jsn("foo"),
          jsn(2)
        )
      ),
      jsnO(
        "some_array", jsnO(
          "custom-matcher", jsn("/*ALL_MATCH*/"),
          "expected", jsn("/*STRING*/")
        )
      )
    );
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"some_array\": [\n" +
        "      \"foo\", //is a string\n" +
        "      2, //expected a string\n" +
        "  ],\n" +
        "}\n")
    );
  }

  @Test
  public void handlesNestedObjects() throws Exception {
    JsonDiffer differ = jsonDiffer().build();

    DiffResult result = differ.diff(
      jsnO(
        "a", jsnO(
          "b", jsn(2),
          "c", jsn("superfluous")
        )
      ),
      jsnO(
        "a", jsnO(
          "b", jsn(2)
        )
      )
    );
    assertThat(result.asConsoleAnsiStripped(),
      is("{\n" +
        "  \"a\": {\n" +
        "      \"b\": 2, //is 2\n" +
        "      \"c\": \"superfluous\", //not part of contract\n" +
        "  },\n" +
        "}\n")
    );
  }
}
