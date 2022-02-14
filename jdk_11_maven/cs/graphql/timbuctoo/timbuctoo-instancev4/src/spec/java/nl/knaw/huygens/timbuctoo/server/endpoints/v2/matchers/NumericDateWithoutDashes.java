package nl.knaw.huygens.timbuctoo.server.endpoints.v2.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MatchingDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MisMatchDiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.Matcher;
import nl.knaw.huygens.contractdiff.jsondiff.Recurser;

import java.util.regex.Pattern;

public class NumericDateWithoutDashes implements Matcher {
  @Override
  public DiffResult match(JsonNode actual, ObjectNode config, Recurser recurse) {
    if (actual.isTextual()) {
      Pattern datePattern = Pattern.compile("[0-9]*");
      if (datePattern.matcher(actual.asText()).matches()) {
        if (actual.asText().length() != 8) {
          return new MisMatchDiffResult("exactly 8 digits (currently the string has " + actual.asText().length() +
            " digits)", actual.toString());
        } else {
          return new MatchingDiffResult("a string containing 8 digits", actual.toString());
        }
      } else {
        return new MisMatchDiffResult("8 digits (0-9)", actual.toString());
      }
    } else {
      return new MisMatchDiffResult("a string containing 8 digits", actual.toString());
    }
  }
}
