package nl.knaw.huygens.timbuctoo.v5.dropwizard;

import java.util.Set;

public interface SupportedExportFormats {
  Set<String> getSupportedMimeTypes();
}
