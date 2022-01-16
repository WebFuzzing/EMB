package nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes;

import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class SerializerWriter implements MessageBodyWriter<SerializableResult> {
  private final SerializationFactory serializationFactory;

  protected SerializerWriter(SerializationFactory serializationFactory) {
    this.serializationFactory = serializationFactory;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return SerializableResult.class.isAssignableFrom(type);
  }

  @Override
  public long getSize(SerializableResult serializableResult, Class<?> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType) {
    return 0;
  }

  @Override
  public void writeTo(SerializableResult serializableResult, Class<?> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {
    serializationFactory.create(entityStream).serialize(serializableResult);
  }

  public abstract String getMimeType() ;

  public SerializationFactory getSerializationFactory() {
    return serializationFactory;
  }
}
