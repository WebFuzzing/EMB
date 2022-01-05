package nl.knaw.huygens.timbuctoo.rml.datasource.jexl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.datasource.JoinHandler;
import nl.knaw.huygens.timbuctoo.rml.datasource.RowFactory;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class JexlRowFactory implements RowFactory {

  private final HashMap<String, JexlExpression> expressions;
  private static final Logger LOG = getLogger(JexlRowFactory.class);
  private final String stringRepresentation;

  @Override
  public JoinHandler getJoinHandler() {
    return joinHandler;
  }

  private final JoinHandler joinHandler;

  public JexlRowFactory(Map<String, String> customFields, JoinHandler joinHandler) {
    this.joinHandler = joinHandler;
    this.expressions = new HashMap<>();
    Map<String, Object> ns = Maps.newHashMap();
    ns.put("Json", JsonEncoder.class); // make method Json:stringify available in expressions
    ns.put("Math", Math.class); // make all methods of Math available
    ns.put("Integer", Integer.class); // make method Integer
    JexlEngine jexl = new JexlBuilder().namespaces(ns).create();
    StringBuilder result = new StringBuilder();
    customFields.forEach((key, value) -> {
      try {
        expressions.put(key, jexl.createExpression(value));
        result.append("      ").append(key).append(": ").append(value).append("\n");
      } catch (Exception e) { // Catch the runtime exceptions
        LOG.error("Could not compile expression '{}'", value);
        LOG.error("Exception thrown", e);
      }
    });
    this.stringRepresentation = result.toString();
  }

  @Override
  public String toString() {
    return stringRepresentation;
  }

  @Override
  public Row makeRow(Map<String, String> values, ErrorHandler errorHandler) {
    return new MapBasedRow(values, joinHandler.resolveReferences(values), errorHandler, expressions);
  }

  private class MapBasedRow implements Row {
    private final Map<String, String> data;
    private final Map<String, List<String>> joinedData;
    private final ErrorHandler errorHandler;
    private final Map<String, JexlExpression> expressions;

    public MapBasedRow(Map<String, String> data, Map<String, List<String>> joinedData, ErrorHandler errorHandler,
                       Map<String, JexlExpression> expressions) {
      this.data = data;
      this.joinedData = joinedData;
      this.errorHandler = errorHandler;
      this.expressions = expressions;
    }

    @Override
    public String getRawValue(String key) {
      if (data.containsKey(key)) {
        return data.get(key);
      } else if (expressions.containsKey(key)) {
        try {
          JexlContext jexlContext = new MapContext();
          jexlContext.set("v", data);
          Object result = expressions.get(key).evaluate(jexlContext);
          if (result != null) {
            return result.toString();
          } else {
            return null;
          }
        } catch (Throwable throwable) {
          LOG.info("Error during mapping", throwable);
          errorHandler.valueGenerateFailed(
            key,
            String.format("Could not execute expression '%s' for row with values: '%s.", expressions.get(key), data)
          );
          return null;
        }
      } else {
        return null;
      }
    }

    @Override
    public List<String> getJoinValue(String key) {
      List<String> result = joinedData.get(key);
      if (result == null) {
        return Lists.newArrayList();
      }
      return result;
    }

    @Override
    public void handleLinkError(String childField, String parentCollection, String parentField) {
      errorHandler.linkError(data, childField, parentCollection, parentField);
    }

    @Override
    public String toString() {
      return "MapBasedRow{" +
        "data=" + data +
        ", joinedData=" + joinedData +
        ", expressions=" + expressions +
        '}';
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
        final MapBasedRow other = (MapBasedRow) obj;
        return Objects.equals(this.data, other.data) &&
          Objects.equals(this.joinedData, other.joinedData) &&
          Objects.equals(this.expressions, other.expressions);
      }
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.data, this.joinedData, this.expressions);
    }
  }
}
