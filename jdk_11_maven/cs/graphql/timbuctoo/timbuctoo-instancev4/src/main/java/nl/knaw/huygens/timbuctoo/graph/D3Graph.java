package nl.knaw.huygens.timbuctoo.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D3Graph {
  public static final Logger LOG = LoggerFactory.getLogger(D3GraphGeneratorService.class);

  // These are linked hash tables because some of the tests rely on the order of
  // their iterators. Is that necessary?
  private final LinkedHashMap<Node, Integer> nodeIndex = new LinkedHashMap<>();
  private final LinkedHashSet<Link> links = new LinkedHashSet<>();

  public void addNode(Vertex vertex, String entityTypeName) {
    try {
      nodeIndex.putIfAbsent(new Node(vertex, entityTypeName), nodeIndex.size());
    } catch (IOException e) {
      LOG.error("Node not added because " + e.getMessage());
    }
  }

  public void addLink(Edge edge, Vertex source, Vertex target, String sourceTypeName, String targetTypeName) {
    try {
      Integer sourceNode = nodeIndex.get(new Node(source, sourceTypeName));
      Integer targetNode = nodeIndex.get(new Node(target, targetTypeName));
      if (sourceNode == null) {
        LOG.error("Link for edge {} was added, but the source vertex {} was not", edge.id(), source.id());
      }
      if (targetNode == null) {
        LOG.error("Link for edge {} was added, but the target vertex {} was not", edge.id(), target.id());
      }
      if (sourceNode != null && targetNode != null) {
        links.add(new Link(edge, sourceNode, targetNode));
      }
    } catch (IOException e) {
      LOG.error("Link not added because " + e.getMessage());
    }
  }

  public List<Node> getNodes() {
    return nodeIndex.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
  }

  public Collection<Link> getLinks() {
    return Collections.unmodifiableSet(links);
  }

  @Override
  public String toString() {
    return "D3Graph{" +
      "nodes=" + getNodes() +
      ", links=" + getLinks() +
      '}';
  }
}
