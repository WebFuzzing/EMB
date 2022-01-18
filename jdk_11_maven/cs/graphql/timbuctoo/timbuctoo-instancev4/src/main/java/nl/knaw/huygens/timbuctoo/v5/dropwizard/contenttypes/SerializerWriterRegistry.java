package nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes;

import nl.knaw.huygens.timbuctoo.v5.dropwizard.SupportedExportFormats;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

public class SerializerWriterRegistry implements SupportedExportFormats {
  private HashMap<String, SerializerWriter> supportedMimeTypes;

  public SerializerWriterRegistry(SerializerWriter... writers) {
    supportedMimeTypes = new HashMap<>();
    for (SerializerWriter writer : writers) {
      register(writer);
    }
  }


  @Override
  public Set<String> getSupportedMimeTypes() {
    return supportedMimeTypes.keySet();
  }

  private void register(SerializerWriter serializerWriter) {
    String mimeType = serializerWriter.getMimeType();
    SerializerWriter added = supportedMimeTypes.putIfAbsent(mimeType, serializerWriter);
    if (added != null) {
      throw new RuntimeException(format("Timbuctoo supports only one serializer writer for '%s'", mimeType));
    }
  }

  public Optional<SerializerWriter> getBestMatch(String acceptHeader) {
    return MimeParser.bestMatch(supportedMimeTypes.keySet(), acceptHeader).map(supportedMimeTypes::get);
  }
}
