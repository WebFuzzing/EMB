package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.model.properties.converters.PersonNamesConverter;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;

public class WwPersonDisplayName extends ReadableProperty {
  private static final Logger LOG = LoggerFactory.getLogger(WwPersonDisplayName.class);
  public static final String TYPE = "wwperson-display-name";

  public WwPersonDisplayName() {
    super(() ->
      __.map(x -> {
        JsonNode displayName = null;
        Vertex vertex = (Vertex) x.get();
        try {
          final PersonNames personNames = vertex.property("wwperson_names").isPresent() ?
            new PersonNamesConverter().tinkerpopToJava(vertex.value("wwperson_names")) : new PersonNames();
          final String parsedName = personNames.defaultName().getFullName();

          if (parsedName.length() > 0) {
            displayName = jsn(parsedName);
          } else {
            final String tempName = vertex.property("wwperson_tempName").isPresent() ?
              vertex.value("wwperson_tempName") : "";

            displayName = jsn(String.format("[TEMP] %s", tempName));
          }
        } catch (IOException e) {
          LOG.error("Parse failure for {} ", vertex.value("wwperson_names").toString());
        }
        return Try.success(displayName);
      }),
      () ->
        __.map(x -> {
          Object displayName = null;
          Vertex vertex = (Vertex) x.get();
          try {
            final PersonNames personNames = vertex.property("wwperson_names").isPresent() ?
              new PersonNamesConverter().tinkerpopToJava(vertex.value("wwperson_names")) : new PersonNames();
            final String parsedName = personNames.defaultName().getShortName();

            if (parsedName.length() > 0) {
              displayName = jsn(parsedName);
            } else {
              final String tempName = vertex.property("wwperson_tempName").isPresent() ?
                vertex.value("wwperson_tempName") : "";

              displayName = String.format("[TEMP] %s", tempName);
            }
          } catch (IOException e) {
            LOG.error("Parse failure for {} ", vertex.value("wwperson_names").toString());
          }
          return Try.success(displayName);
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
