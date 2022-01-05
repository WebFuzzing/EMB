package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import static nl.knaw.huygens.timbuctoo.rdf.RdfProperties.RDF_SYNONYM_PROP;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class RdfImportedDefaultDisplayname extends ReadableProperty {
  public static final String TYPE = "default-rdf-imported-displayname";

  public RdfImportedDefaultDisplayname() {
    super(() ->
        __.<Vertex, Try<JsonNode>>map(traverser -> {
          final Vertex vertex = traverser.get();
          if (vertex.property(RDF_SYNONYM_PROP).isPresent() && vertex.<String[]>value(RDF_SYNONYM_PROP).length > 0) {
            return Try.success(jsn(vertex.<String[]>value(RDF_SYNONYM_PROP)[0]));
          }
          return Try.success(jsn("<no rdf uri found>"));
        }),
      () -> __.<Vertex, Try<Object>>map(traverser -> {
        final Vertex vertex = traverser.get();
        if (vertex.property(RDF_SYNONYM_PROP).isPresent() && vertex.<String[]>value(RDF_SYNONYM_PROP).length > 0) {
          return Try.success(vertex.<String[]>value(RDF_SYNONYM_PROP));
        }
        return Try.success("<no rdf uri found>");
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
