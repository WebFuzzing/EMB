package nl.knaw.huygens.contractdiff.jsondiff;

import com.fasterxml.jackson.databind.JsonNode;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;

public interface Recurser {
  DiffResult recurser(JsonNode actual, JsonNode expected);
}
