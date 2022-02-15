package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

public class PropertyOrDefault extends ReadableProperty {

  public PropertyOrDefault(ReadableProperty prop, JsonNode orElse) {
    super(() ->
      __.<Object, Try<JsonNode>>coalesce(
        prop.traversalJson(),
        __.map(x -> Try.success(orElse))
      ),
      () ->
        __.coalesce(
          prop.traversalRaw(),
          __.map(x -> Try.success((Object) orElse.asText()))
        )
    );
  }

  public PropertyOrDefault(ReadableProperty prop, ReadableProperty orElse) {
    super(() ->
      __.<Object, Try<JsonNode>>coalesce(
        prop.traversalJson(),
        orElse.traversalJson()
      ),
      () ->
        __.coalesce(
          prop.traversalRaw(),
          orElse.traversalRaw()
        )
    );
  }

  @Override
  public String getTypeId() {
    return "property-or-default";
  }

  @Override
  public String getUniqueTypeId() {
    return  "property-or-default";
  }
}
