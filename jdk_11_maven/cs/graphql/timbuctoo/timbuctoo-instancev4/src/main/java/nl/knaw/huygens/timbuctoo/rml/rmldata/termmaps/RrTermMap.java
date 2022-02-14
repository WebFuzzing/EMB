package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;

import java.util.Optional;

public interface RrTermMap {
  Optional<QuadPart> generateValue(Row input);

}
