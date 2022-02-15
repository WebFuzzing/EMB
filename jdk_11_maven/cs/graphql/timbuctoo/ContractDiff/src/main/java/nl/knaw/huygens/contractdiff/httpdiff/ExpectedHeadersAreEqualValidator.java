package nl.knaw.huygens.contractdiff.httpdiff;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MatchingDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MisMatchDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MissingPropertyDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.SuperfluousPropertyDiffResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nl.knaw.huygens.contractdiff.JsonBuilder.jsn;

public class ExpectedHeadersAreEqualValidator {

  private enum DiffType {
    MATCH,
    SUPERFLUOUS,
    MISMATCH
  }


  private static class DoDiff {
    private final List<String> actuals;
    int headerCount;
    int headersDone;
    DiffType type;
    DiffType restType;
    int headerToMark;
    String expectedValue;

    public DoDiff(List<String> expectation, List<String> actuals) {
      this.actuals = actuals;
      headersDone = 0;

      if (expectation == null || expectation.size() == 0) {
        type = DiffType.SUPERFLUOUS;
        restType = DiffType.SUPERFLUOUS;
        headerToMark = 0;
      } else if (Strings.isNullOrEmpty(expectation.get(0))) {
        type = DiffType.MATCH;
        restType = DiffType.SUPERFLUOUS;
        headerToMark = 0;
      } else {
        expectedValue = expectation.get(0);
        type = DiffType.MISMATCH;
        restType = DiffType.MISMATCH;
        for (int i = 0; i < actuals.size(); i++) {
          String actual = actuals.get(i);
          if (Objects.equals(actual, expectedValue)) {
            type = DiffType.MATCH;
            restType = DiffType.SUPERFLUOUS;
            headerToMark = i;
          }
        }
      }
    }

    public DiffResult makeResult(DiffType diffType, String actual) {
      switch (diffType) {
        case MATCH:
          if (expectedValue == null) {
            return new MatchingDiffResult("provided", actual);
          } else {
            return new MatchingDiffResult(jsn(expectedValue).toString(), actual);
          }
        case MISMATCH:
          return new MisMatchDiffResult(jsn(expectedValue).toString(), actual);
        case SUPERFLUOUS:
          return new SuperfluousPropertyDiffResult(actual);
        default:
          throw new RuntimeException("Not all cases of the DiffType ENUM have been handled");
      }
    }

    public DiffResult getNextResult() {
      DiffResult result;
      if (headersDone == headerToMark) {
        result = makeResult(type, actuals.get(headersDone));
      } else {
        result = makeResult(restType, actuals.get(headersDone));
      }
      headersDone += 1;
      return result;
    }
  }

  public static HttpHeadersDiffResult validate(LinkedListMultimap<String, String> expectation,
                                        LinkedListMultimap<String, String> reality) {

    final HttpHeadersDiffResult result = new HttpHeadersDiffResult();

    //Invariants:
    //We don't supporting multiple expectations for headers of the same name
    for (String fieldName : expectation.keySet()) {
      if (expectation.get(fieldName).size() > 1) {
        result.addMultiHeaderExpectation(fieldName);
      }
    }

    //Preparations
    Map<String, DoDiff> diffResults = new HashMap<>();
    for (String key : reality.keySet()) {
      diffResults.put(key, new DoDiff(expectation.get(key), reality.get(key)));
    }

    //output results
    for (Map.Entry<String, String> actual : reality.entries()) {
      result.add(actual.getKey(), diffResults.get(actual.getKey()).getNextResult());
    }
    for (String key : expectation.keySet()) {
      if (!reality.containsKey(key)) {
        result.add(key, new MissingPropertyDiffResult(expectation.get(key).get(0)));
      }
    }
    return result;
  }
}
