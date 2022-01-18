package nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes;

import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.JsonLdSerialization;

import javax.ws.rs.Produces;

@Produces(JsonLdWriter.MIME_TYPE)
public class JsonLdWriter extends SerializerWriter {

  public static final String MIME_TYPE = "application/ld+json";

  public JsonLdWriter() {
    super(JsonLdSerialization::new);
  }

  @Override
  public String getMimeType() {
    return MIME_TYPE;
  }
}
