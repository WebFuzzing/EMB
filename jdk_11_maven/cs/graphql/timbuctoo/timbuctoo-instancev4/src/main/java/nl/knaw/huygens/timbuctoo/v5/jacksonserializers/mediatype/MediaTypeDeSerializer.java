package nl.knaw.huygens.timbuctoo.v5.jacksonserializers.mediatype;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class MediaTypeDeSerializer extends StdDeserializer<MediaType> {

  public MediaTypeDeSerializer() {
    super(MediaType.class);
  }

  @Override
  public MediaType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
    throws IOException {
    TextNode node = jsonParser.getCodec().readTree(jsonParser);

    return MediaType.valueOf(node.asText());
  }
}
