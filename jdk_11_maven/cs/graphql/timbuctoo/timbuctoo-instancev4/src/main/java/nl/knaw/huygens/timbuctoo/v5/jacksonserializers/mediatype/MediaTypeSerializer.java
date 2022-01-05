package nl.knaw.huygens.timbuctoo.v5.jacksonserializers.mediatype;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class MediaTypeSerializer extends StdSerializer<MediaType> {

  public MediaTypeSerializer() {
    super(MediaType.class);
  }

  @Override
  public void serialize(MediaType mediaType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
    throws IOException {
    jsonGenerator.writeString(mediaType.toString());
  }
}
