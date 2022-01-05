package nl.knaw.huygens.timbuctoo.search;

import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchRequestV2_1;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public interface SearchDescription {
  String ID_DB_PROP = "tim_id";

  List<String> getSortableFields();

  List<String> getFullTextSearchFields();

  EntityRef createRef(Vertex vertex);

  SearchResult execute(GraphWrapper graphWrapper, SearchRequestV2_1 searchRequest);

  String getType();
}
