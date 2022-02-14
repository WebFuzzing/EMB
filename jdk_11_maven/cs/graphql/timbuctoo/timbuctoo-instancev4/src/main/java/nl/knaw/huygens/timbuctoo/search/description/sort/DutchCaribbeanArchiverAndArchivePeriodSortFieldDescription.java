package nl.knaw.huygens.timbuctoo.search.description.sort;

import com.google.common.collect.Lists;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;

import java.util.ArrayList;
import java.util.List;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;

public class DutchCaribbeanArchiverAndArchivePeriodSortFieldDescription implements SortFieldDescription {


  private final String name;
  private final ArrayList<GraphTraversal<Object, Object>> traversals;

  @SuppressWarnings("unchecked")
  public DutchCaribbeanArchiverAndArchivePeriodSortFieldDescription(String name, String beginDate, String endDate) {
    this.name = name;
    traversals = Lists.newArrayList(
      localProperty().withName(beginDate).build().getTraversal(),
      localProperty().withName(endDate).build().getTraversal()
    );
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<GraphTraversal<Object, Object>> getTraversal() {
    return traversals;
  }
}
