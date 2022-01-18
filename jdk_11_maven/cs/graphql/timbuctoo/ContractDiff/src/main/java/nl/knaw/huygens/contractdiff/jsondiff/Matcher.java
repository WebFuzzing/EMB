package nl.knaw.huygens.contractdiff.jsondiff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;

@FunctionalInterface
public interface Matcher {
  DiffResult match(JsonNode actual, ObjectNode config, Recurser recurse);
}
