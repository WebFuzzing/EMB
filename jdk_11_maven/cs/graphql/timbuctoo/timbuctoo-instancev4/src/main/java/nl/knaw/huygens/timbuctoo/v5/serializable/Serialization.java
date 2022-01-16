package nl.knaw.huygens.timbuctoo.v5.serializable;

import java.io.IOException;

public interface Serialization {
  void serialize(SerializableResult serializableResult) throws IOException;
}
