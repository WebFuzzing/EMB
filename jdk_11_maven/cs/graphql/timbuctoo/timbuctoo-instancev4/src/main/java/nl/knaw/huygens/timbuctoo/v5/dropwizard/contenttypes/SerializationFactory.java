package nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes;

import nl.knaw.huygens.timbuctoo.v5.serializable.Serialization;

import java.io.IOException;
import java.io.OutputStream;

public interface SerializationFactory {
  Serialization create(OutputStream outputStream) throws IOException;
}
