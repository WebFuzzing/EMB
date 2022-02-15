package nl.knaw.huygens.timbuctoo.v5.jacksonserializers;

import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.knaw.huygens.timbuctoo.v5.jacksonserializers.mediatype.MediaTypeDeSerializer;
import nl.knaw.huygens.timbuctoo.v5.jacksonserializers.mediatype.MediaTypeSerializer;

import javax.ws.rs.core.MediaType;

public class TimbuctooCustomSerializers extends SimpleModule {
  public TimbuctooCustomSerializers() {
    this.addDeserializer(MediaType.class, new MediaTypeDeSerializer());
    this.addSerializer(MediaType.class, new MediaTypeSerializer());
  }
}
