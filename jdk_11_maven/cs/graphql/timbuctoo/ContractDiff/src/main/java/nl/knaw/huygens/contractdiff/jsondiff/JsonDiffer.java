package nl.knaw.huygens.contractdiff.jsondiff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Sets;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MatchingDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MisMatchDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MissingPropertyDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.SuperfluousPropertyDiffResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

import static nl.knaw.huygens.contractdiff.JsonBuilder.jsn;
import static nl.knaw.huygens.contractdiff.JsonBuilder.jsnO;

public class JsonDiffer {
  private final Map<String, Matcher> handlers;
  private final String handleArraysWith;
  private final Function<JsonNode, ObjectNode> arrayMatcherConfigAdapter;

  public JsonDiffer(Map<String, Matcher> handlers,
                    String handleArraysWith, Function<JsonNode, ObjectNode> arrayMatcherConfigAdapter) {
    this.handlers = handlers;
    this.handleArraysWith = handleArraysWith;
    this.arrayMatcherConfigAdapter = arrayMatcherConfigAdapter;
  }

  public DiffResult diff(JsonNode actual, JsonNode expected) {
    return checkNodes(actual, expected);
  }

  public DiffResult diff(String actualStr, String expectedStr) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actual = mapper.readTree(actualStr);
    JsonNode expected = mapper.readTree(expectedStr);
    return diff(actual, expected);
  }

  public DiffResult diff(JsonNode actual, String expectedStr) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode expected = mapper.readTree(expectedStr);
    return diff(actual, expected);

  }

  public DiffResult diff(String actualStr, JsonNode expected) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actual = mapper.readTree(actualStr);
    return diff(actual, expected);

  }

  public DiffResult checkNodes(JsonNode actual, JsonNode expected) {
    if (expected == null) {
      return new MisMatchDiffResult("null", actual.toString());
    }
    Function<JsonNode, DiffResult> expectedFunc = getExpectedFuncFor(expected);
    if (expectedFunc != null) {
      return expectedFunc.apply(actual);
    } else if (actual.isArray() && handlers.containsKey(handleArraysWith)) {
      ObjectNode config = arrayMatcherConfigAdapter.apply(expected.deepCopy());
      config.set("custom-matcher", jsn(handleArraysWith));
      return handlers.get(handleArraysWith).match(actual, config, this::checkNodes);
    } else if (expected.getNodeType() == actual.getNodeType()) {
      if (actual.isObject()) {
        return checkObject((ObjectNode) actual, (ObjectNode) expected);
      } else {
        return checkValue(actual, expected);
      }
    } else {
      return new MisMatchDiffResult(expected.toString(), actual.toString());
    }
  }

  private Function<JsonNode, DiffResult> getExpectedFuncFor(JsonNode expected) {
    if (expected.isTextual()) {
      String token = expected.asText();
      if (handlers.containsKey(token)) {
        //fake an empty configuration object
        ObjectNode config = jsnO("custom-matcher", expected);
        return (node) -> handlers.get(token).match(node, config, this::checkNodes);
      } else if (token.startsWith("/*") && token.endsWith("*/")) {
        throw new RuntimeException("Token " + token.substring(2, token.length() - 2) + " was not registered");
      }
    } else if (expected.has("custom-matcher")) {
      String token = expected.get("custom-matcher").asText();
      if (handlers.containsKey(token)) {
        return (node) -> handlers.get(token).match(node, (ObjectNode) expected, this::checkNodes);
      } else {
        if (token.startsWith("/*") && token.endsWith("*/")) {
          throw new RuntimeException("Token " + token.substring(2, token.length() - 2) + " was not registered");
        }
      }
    }
    return null;
  }

  public DiffResult checkValue(JsonNode actual, JsonNode expected) {
    if (Objects.equals(actual, expected)) {
      return new MatchingDiffResult(expected.toString(), actual.toString());
    } else {
      return new MisMatchDiffResult(expected.toString(), actual.toString());
    }
  }

  public static DiffResult assertThat(boolean result, String message, JsonNode node) {
    if (result) {
      return new MatchingDiffResult(message, node.toString());
    } else {
      return new MisMatchDiffResult(message, node.toString());
    }
  }

  //FIXME: handle arrays as expected and arrays as actual
  public DiffResult checkObject(ObjectNode actual, ObjectNode expected) {
    Set<String> expectedFields = Sets.newHashSet();
    expected.fieldNames().forEachRemaining(expectedFields::add);
    ObjectDiffResult result = new ObjectDiffResult();

    actual.fields().forEachRemaining(field -> {
      //we've seen this one
      expectedFields.remove(field.getKey());
      if (expected.get(field.getKey()) == null) {
        result.add(field.getKey(), new SuperfluousPropertyDiffResult(field.getValue().toString()));
      } else {
        result.add(field.getKey(), checkNodes(field.getValue(), expected.get(field.getKey())));
      }
    });
    expectedFields.forEach(field -> result.add(field, new MissingPropertyDiffResult(expected.get(field).toString())));

    return result;
  }

  public static JsonDifferBuilder jsonDiffer() {
    return new JsonDifferBuilder();
  }

  public static class JsonDifferBuilder {
    Map<String, Matcher> handlers;
    private String handleArraysWith = "/*IS_SAME_ARRAY*/";
    private Function<JsonNode, ObjectNode> arrayMatcherConfigAdapter = (config) -> jsnO("array", config);

    public JsonDifferBuilder() {
      handlers = new HashMap<>();
      withCustomHandler("ALL_MATCH", (actual, config, recurse) -> {
        if (actual.isArray()) {
          ArrayDiffResult result = new ArrayDiffResult();
          JsonNode expected = config.get("expected");
          for (int i = 0; i < actual.size(); i++) {
            result.add(i, recurse.recurser(actual.get(i), expected));
          }
          return result;
        } else {
          return new MisMatchDiffResult("an array", actual.toString());
        }
      });
      withCustomHandler("IS_SAME_ARRAY", (actual, config, recurser) -> {
        ArrayDiffResult result = new ArrayDiffResult();
        JsonNode expectation = config.get("array");
        for (int i = 0; i < actual.size(); i++) {
          result.add(i, recurser.recurser(actual.get(i), expectation.get(i)));
        }
        int startIndex =  actual.size() -1;
        if(startIndex<0)
        	startIndex = 0;
        if (expectation.size() > actual.size()) {
          for (int i = startIndex; i < expectation.size(); i++) {
            result.add(i, new MissingPropertyDiffResult(""+expectation.get(i)));
          }
        }
        return result;
      });
      withCustomHandler("ALL_MATCH_ONE_OF", (actual, config, recurse) -> {
        if (actual.isArray()) {
          if (config.has("invariant")) {
            ArrayDiffResult result = new ArrayDiffResult();
            JsonNode expectedItem = config.get("invariant");
            for (int i = 0; i < actual.size(); i++) {
              JsonNode actualItem = actual.get(i);
              result.add(i, recurse.recurser(actualItem, expectedItem));
            }
            return result;
          } else {
            ArrayDiffResult result = new ArrayDiffResult();
            JsonNode expected = config.get("possibilities");
            if (!(expected instanceof ObjectNode)) {
              return assertThat(false, "a property called \"possibilities\" in the expectation containing an " +
                  "object with the various expectations indexed by the value of the property referenced by the " +
                  "\"keyProp\" of the actual",
                config);
            }
            JsonNode keyProp = config.get("keyProp");
            String key;
            if (keyProp instanceof TextNode) {
              key = keyProp.asText();
            } else {
              return assertThat(false, "a property called \"keyProp\" containing the name of the property of the " +
                  "actual item that will be used to look up the corresponding expectation",
                config);
            }

            for (int i = 0; i < actual.size(); i++) {
              JsonNode actualItem = actual.get(i);
              JsonNode typeProp = actualItem.get(key);
              if (typeProp == null) {
                result.add(i, recurse.recurser(actualItem, jsnO(key, jsn("/*STRING*/")))); //show that the key is missing
                continue;
              }
              JsonNode expectedItem = expected.get(typeProp.asText());
              if (expectedItem == null) {
                result.add(i, assertThat(false, "No expectation for '" + key + "'", actualItem));
                continue;
              }
              result.add(i, recurse.recurser(actualItem, expectedItem));
            }
            return result;
          }
        } else {
          return new MisMatchDiffResult("an array", actual.toString());
        }
      });

      withCustomHandler("STRING", (actual) -> {
        if (actual.isTextual()) {
          return new MatchingDiffResult("a string", actual.toString());
        } else {
          return new MisMatchDiffResult("a string", actual.toString());
        }
      });
      withCustomHandler("STRING_OR_NULL", (actual) -> {
        if (actual.isTextual() || actual.isNull()) {
          return new MatchingDiffResult("a string or a null", actual.toString());
        } else {
          return new MisMatchDiffResult("a string or a null", actual.toString());
        }
      });

      withCustomHandler("NUMBER", (actual) -> {
        if (actual.isNumber()) {
          return new MatchingDiffResult("a number", actual.toString());
        } else {
          return new MisMatchDiffResult("a number", actual.toString());
        }
      });

      withCustomHandler("DATE_STRING", (actual) -> {
        if (actual.isTextual()) {
          Pattern datePattern = Pattern.compile("[0-9-]+");
          if (datePattern.matcher(actual.asText()).matches()) {
            return new MatchingDiffResult("a number", actual.toString());
          } else {
            return new MisMatchDiffResult("only 0-9 or a '-'", actual.toString());
          }
        } else {
          return new MisMatchDiffResult("a string containing 0-9 or a -", actual.toString());
        }
      });

    }

    public JsonDifferBuilder withCustomHandler(String token, Function<JsonNode, DiffResult> handler) {
      return withCustomHandler(token, (node, config, recurse) -> handler.apply(node));
    }

    public JsonDifferBuilder withCustomHandler(String token, BiFunction<JsonNode, ObjectNode, DiffResult> handler) {
      return withCustomHandler(token, (node, config, recurse) -> handler.apply(node, config));
    }

    public JsonDifferBuilder withCustomHandler(String token, Matcher handler) {
      if (this.handlers.containsKey(token)) {
        throw new RuntimeException("Custom matcher already defined for " + token);
      }
      handlers.put("/*" + token + "*/", handler);
      return this;
    }

    public JsonDifferBuilder handleArraysWith(String handlerName, Function<JsonNode, ObjectNode> adapter) {
      this.handleArraysWith = "/*" + handlerName + "*/";
      this.arrayMatcherConfigAdapter = adapter;
      return this;
    }

    public JsonDiffer build() {
      return new JsonDiffer(handlers, handleArraysWith, arrayMatcherConfigAdapter);
    }
  }
}
