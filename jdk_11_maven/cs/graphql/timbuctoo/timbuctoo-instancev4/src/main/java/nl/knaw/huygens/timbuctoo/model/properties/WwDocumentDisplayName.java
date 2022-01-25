package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.datable;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class WwDocumentDisplayName extends ReadableProperty {

  public static final String TYPE = "wwdocument-display-name";

  public WwDocumentDisplayName() {
    super(() ->
        __.as("doc").union(
          new PropertyOrDefault(localProperty("wwdocument_date", datable), jsn("")).traversalJson()
        ).as("date")
          .select("doc").union(
          new PropertyOrDefault(localProperty("wwdocument_title"), jsn("")).traversalJson()
        ).as("title")
          .select("title", "date")
          .map(x -> {
            Try<JsonNode> date = (Try<JsonNode>) x.get().get("date");
            Try<JsonNode> title = (Try<JsonNode>) x.get().get("title");
            return Try.success((JsonNode) jsn(
              title.getOrElse(jsn("")).asText() +
                " (" + date.getOrElse(jsn("")).asText() + ")"
            ));
          }),
      () ->
        __.as("doc").union(
          new PropertyOrDefault(localProperty("wwdocument_date", datable), jsn("")).traversalRaw()
        ).as("date")
          .select("doc").union(
          new PropertyOrDefault(localProperty("wwdocument_title"), jsn("")).traversalRaw()
        ).as("title")
          .select("title", "date")
          .map(x -> {
            Try<Object> date = (Try<Object>) x.get().get("date");
            Try<Object> title = (Try<Object>) x.get().get("title");
            return Try.success((Object) (title.getOrElse("") +
              " (" + date.getOrElse(jsn("")) + ")"
            ));
          })
    );
  }

  @Override
  public String getTypeId() {
    return TYPE;
  }

  @Override
  public String getUniqueTypeId() {
    return TYPE;
  }
}
