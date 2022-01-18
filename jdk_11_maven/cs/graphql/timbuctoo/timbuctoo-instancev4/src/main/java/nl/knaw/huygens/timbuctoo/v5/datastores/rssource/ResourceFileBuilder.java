package nl.knaw.huygens.timbuctoo.v5.datastores.rssource;

import nl.knaw.huygens.timbuctoo.v5.dataset.CurrentStateRetriever;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ResourceFileBuilder {

  private final String graph;
  private DataSetQuadGenerator dataSetQuadGenerator;

  public ResourceFileBuilder(String graph) {
    this.graph = graph;
    this.dataSetQuadGenerator = new DataSetQuadGenerator(graph);
  }

  public Stream<String> retrieveData(CurrentStateRetriever currentStateRetriever) {
    return currentStateRetriever.retrieveData().map(quad -> {
        Optional<String> dataType = quad.getValuetype();
        if (dataType == null || !dataType.isPresent()) {
          return dataSetQuadGenerator.onRelation(quad.getSubject(), quad.getPredicate(), quad.getObject(), graph);
        } else {
          Optional<String> language = quad.getLanguage();
          String dataTypeString = dataType.get();
          if (language != null && language.isPresent() && dataTypeString.equals(RdfConstants.LANGSTRING)) {
            return dataSetQuadGenerator.onLanguageTaggedString(
              quad.getSubject(),
              quad.getPredicate(),
              quad.getObject(),
              language.get(),
              graph
            );
          } else {
            return dataSetQuadGenerator.onValue(
              quad.getSubject(),
              quad.getPredicate(),
              quad.getObject(),
              dataTypeString,
              graph
            );
          }
        }
      }
    );
  }
}
