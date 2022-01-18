package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.stream.Stream;

public class JsonBuilder {

  /*
    If you need to turn a json document into the builder syntax you can use the following javascript code:

    ```
    JSON.parse(myjsonString, function objToJsnBuilder(key, value) {
      var result;
      if (value instanceof Array) {
        return "jsnA(\n" + value.map(item => item.split("\n").map(x =>"  "+x).join("\n")).join(",\n") + "\n)";
      } else if (value != null && typeof value === "object") {
        result = [];
        for (var key in value) {
          if (value[key].indexOf("\n") > -1) {
            result.push("  " + "tuple(" + JSON.stringify(key) + ", " + value[key].split("\n").map(x => "  "+x)
              .join("\n").substr(2) + ")");
          } else {
            result.push("  " + "tuple(" + JSON.stringify(key) + ", " + value[key] + ")");
          }
        }
        return "jsnO(\n" + result.join(",\n") + "\n)";
      } else if (value == null) {
        return "jsn()";
      } else {
        return "jsn(" + JSON.stringify(value) + ")";
      }
    });
    ```

    It will output a string with the builder java code.
   */

  public static JsonNodeFactory factory = JsonNodeFactory.instance;

  public static ObjectNode jsnO(String prop1, JsonNode contents1) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1, String prop2, JsonNode contents2) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1,
                                String prop2, JsonNode contents2,
                                String prop3, JsonNode contents3) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    result.set(prop3, contents3);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1,
                                String prop2, JsonNode contents2,
                                String prop3, JsonNode contents3,
                                String prop4, JsonNode contents4) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    result.set(prop3, contents3);
    result.set(prop4, contents4);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1,
                                String prop2, JsonNode contents2,
                                String prop3, JsonNode contents3,
                                String prop4, JsonNode contents4,
                                String prop5, JsonNode contents5) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    result.set(prop3, contents3);
    result.set(prop4, contents4);
    result.set(prop5, contents5);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1,
                                String prop2, JsonNode contents2,
                                String prop3, JsonNode contents3,
                                String prop4, JsonNode contents4,
                                String prop5, JsonNode contents5,
                                String prop6, JsonNode contents6) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    result.set(prop3, contents3);
    result.set(prop4, contents4);
    result.set(prop5, contents5);
    result.set(prop6, contents6);
    return result;
  }

  public static ObjectNode jsnO(String prop1, JsonNode contents1,
                                String prop2, JsonNode contents2,
                                String prop3, JsonNode contents3,
                                String prop4, JsonNode contents4,
                                String prop5, JsonNode contents5,
                                String prop6, JsonNode contents6,
                                String prop7, JsonNode contents7) {
    ObjectNode result = factory.objectNode();
    result.set(prop1, contents1);
    result.set(prop2, contents2);
    result.set(prop3, contents3);
    result.set(prop4, contents4);
    result.set(prop5, contents5);
    result.set(prop6, contents6);
    result.set(prop7, contents7);
    return result;
  }

  @SafeVarargs
  public static ObjectNode jsnO(Tuple<String, JsonNode>... props) {
    ObjectNode result = factory.objectNode();

    for (Tuple<String, JsonNode> prop: props) {
      result.set(prop.getLeft(), prop.getRight());
    }
    return result;
  }

  public static ObjectNode jsnO() {
    return factory.objectNode();
  }

  public static TextNode jsn(String val) {
    return factory.textNode(val);
  }

  public static NumericNode jsn(int val) {
    return factory.numberNode(val);
  }

  public static NumericNode jsn(long val) {
    return factory.numberNode(val);
  }

  public static BooleanNode jsn(boolean val) {
    return factory.booleanNode(val);
  }

  public static NullNode jsn() {
    return factory.nullNode();
  }

  public static ArrayNode jsnA(JsonNode... contents1) {
    ArrayNode result = factory.arrayNode();
    for (JsonNode item : contents1) {
      result.add(item);
    }
    return result;
  }

  public static ArrayNode jsnA(Stream<? extends JsonNode> contents) {
    ArrayNode result = factory.arrayNode();
    contents.forEachOrdered(result::add);
    return result;
  }

  public static Tuple<String, JsonNode> prp(String key, JsonNode value) {
    return Tuple.tuple(key, value);
  }

}
