package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.databind.JsonNode;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;

public class JsonContractMatcher extends TypeSafeMatcher<JsonNode> {
  private final JsonNode expected;
  private final JsonDiffer differ;
  private DiffResult result;

  private JsonContractMatcher(JsonNode expected, JsonDiffer differ) {
    this.expected = expected;
    this.differ = differ;
  }

  public static JsonContractMatcher matchesContract(JsonNode contract) {
    return new JsonContractMatcher(contract, jsonDiffer().build());
  }

  @Override
  protected boolean matchesSafely(JsonNode actual) {
    result = differ.diff(actual, expected);
    return result.wasSuccess();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("A json object matching the contract");
  }

  @Override
  protected void describeMismatchSafely(JsonNode item, Description mismatchDescription) {
    mismatchDescription.appendText("\n" + result.asConsole());
  }
}
