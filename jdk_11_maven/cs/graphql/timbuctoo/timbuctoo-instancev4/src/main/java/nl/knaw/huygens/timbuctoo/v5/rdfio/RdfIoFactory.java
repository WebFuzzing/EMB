package nl.knaw.huygens.timbuctoo.v5.rdfio;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;

import javax.ws.rs.core.MediaType;
import java.io.OutputStream;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface RdfIoFactory {
  RdfParser makeRdfParser(CachedLog log);

  RdfSerializer makeRdfSerializer(OutputStream output);

  RdfPatchSerializer makeRdfPatchSerializer(OutputStream output, String baseUri);

  boolean isRdfTypeSupported(MediaType mediaType);
}
