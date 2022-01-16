package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;

public class LazyTypeSubjectReference implements SubjectReference {
  private final String subjectUri;
  private final DataSet dataSet;
  private Set<String> types;

  public LazyTypeSubjectReference(String subjectUri, DataSet dataSet) {
    this.subjectUri = subjectUri;
    this.dataSet = dataSet;
  }

  @Override
  public String getSubjectUri() {
    return subjectUri;
  }

  @Override
  public Set<String> getTypes() {
    if (types == null) {
      try (Stream<CursorQuad> quads = dataSet.getQuadStore().getQuads(subjectUri, RDF_TYPE, Direction.OUT, "")) {
        types = quads
          .map(CursorQuad::getObject)
          .collect(toSet());
      }
    }
    return types;
  }

  @Override
  public DataSet getDataSet() {
    return dataSet;
  }
}
