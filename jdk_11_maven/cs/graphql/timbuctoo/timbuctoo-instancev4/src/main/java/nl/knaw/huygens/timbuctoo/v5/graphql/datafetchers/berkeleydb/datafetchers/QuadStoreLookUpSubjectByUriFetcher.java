package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.LookUpSubjectByUriFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

public class QuadStoreLookUpSubjectByUriFetcher implements LookUpSubjectByUriFetcher {

  public QuadStoreLookUpSubjectByUriFetcher() {
  }

  @Override
  public SubjectReference getItem(String uri, DataSet dataSet) {
    return new LazyTypeSubjectReference(uri, dataSet);
  }
}
