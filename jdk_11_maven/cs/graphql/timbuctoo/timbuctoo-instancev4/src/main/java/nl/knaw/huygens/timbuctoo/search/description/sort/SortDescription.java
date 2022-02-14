package nl.knaw.huygens.timbuctoo.search.description.sort;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SortParameter;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SortDescription {
  private final List<SortFieldDescription> sortFieldDescriptions;

  public SortDescription(List<SortFieldDescription> sortFieldDescriptions) {
    this.sortFieldDescriptions = sortFieldDescriptions;
  }

  public void sort(GraphTraversal<Vertex, Vertex> searchResult, List<SortParameter> sortParameters) {
    if (sortParameters.isEmpty()) {
      return;
    }
    List<SortTraversal> traversals = collectTraversals(sortParameters);

    if (traversals.isEmpty()) {
      return;
    }

    searchResult.order();
    for (SortTraversal sortTraversal : traversals) {
      for (GraphTraversal<Object, Object> traversal : sortTraversal.traversals) {
        searchResult.by(traversal, sortTraversal.order);
      }
    }

  }

  // make sure the the sort traversals are in the same order as the parameters sortParameters.
  private List<SortTraversal> collectTraversals(List<SortParameter> sortParameters) {
    List<SortTraversal> traversals = Lists.newArrayList();
    sortParameters.forEach(param -> {
      Optional<SortFieldDescription> description =
        sortFieldDescriptions.stream().filter(desc -> Objects.equals(desc.getName(), param.getFieldName())).findFirst();
      if (description.isPresent()) {

        traversals.add(new SortTraversal(description.get(), param.getDirection()));
      }
    });

    return traversals;
  }

  private static class SortTraversal {

    private final List<GraphTraversal<Object, Object>> traversals;
    private final Order order;

    public SortTraversal(SortFieldDescription sortFieldDescription, SortParameter.Direction direction) {
      traversals = sortFieldDescription.getTraversal();
      order = direction.toOrder();
    }
  }
}
