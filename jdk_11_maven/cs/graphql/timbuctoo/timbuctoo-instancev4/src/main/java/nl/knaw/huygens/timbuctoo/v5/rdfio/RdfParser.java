package nl.knaw.huygens.timbuctoo.v5.rdfio;

import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingParseException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;
import nl.knaw.huygens.timbuctoo.v5.dataset.RdfProcessor;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;

public interface RdfParser {
  void importRdf(CachedLog input, String baseUri, String defaultGraph,
                 RdfProcessor rdfProcessor) throws RdfProcessingFailedException, RdfProcessingParseException;
}
