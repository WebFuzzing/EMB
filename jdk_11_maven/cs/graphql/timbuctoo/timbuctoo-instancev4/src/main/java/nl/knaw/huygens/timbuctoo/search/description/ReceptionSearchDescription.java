package nl.knaw.huygens.timbuctoo.search.description;

import nl.knaw.huygens.timbuctoo.search.EntityRef;
import nl.knaw.huygens.timbuctoo.search.SearchResult;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.List;

public class ReceptionSearchDescription extends WwDocumentSearchDescription {
  private final SearchResult otherSearch;


  private static final String[] DOCUMENT_RELATIONS = {
    "hasEdition",
    "hasSequel",
    "hasTranslation",
    "hasAdaptation",
    "hasPlagiarismBy",
    "isAnnotatedIn",
    "hasBibliography",
    "isCensoredBy",
    "isWorkCommentedOnIn",
    "containedInAnthology",
    "isCopiedBy",
    "isWorkAwarded",
    "hasPreface",
    "isIntertextualOf",
    "isWorkListedOn",
    "isWorkMentionedIn",
    "isParodiedBy",
    "isWorkQuotedIn",
    "isWorkReferencedIn",
    "hasDocumentSource"
  };

  private static final String[] PERSON_RELATIONS = {
    "hasBiography",
    "isPersonCommentedOnIn",
    "isDedicatedPersonOf",
    "isPersonAwarded",
    "isPersonListedOn",
    "isPersonMentionedIn",
    "hasObituary",
    "isPersonQuotedIn",
    "isPersonReferencedIn"
  };



  public ReceptionSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                    FacetDescriptionFactory facetDescriptionFactory, SearchResult otherSearch) {
    super(propertyDescriptorFactory, facetDescriptionFactory);
    this.otherSearch = otherSearch;
    getFacetDescriptions().add(
            facetDescriptionFactory.createEdgeFacetDescription("dynamic_s_relation", getRelationNames())
    );
  }

  private String[] getRelationNames() {
    if (otherSearch.getSearchDescription().getType().equals("wwperson")) {
      return PERSON_RELATIONS;
    } else {
      return DOCUMENT_RELATIONS;
    }
  }

  @Override
  protected GraphTraversal<Vertex, Vertex> initializeVertices(GraphWrapper graphWrapper) {
    // Get the stored search results
    List<Vertex> otherResults = otherSearch.getSearchResult();

    if (otherResults == null) {
      return EmptyGraph.instance().traversal().V();
    }
    // Filter by type as normal
    GraphTraversal<Vertex, Vertex> vertices = graphWrapper.getCurrentEntitiesFor(getType());

    // Return all the vertices which have a reception relation to the store results (other results):
    // The fact that all reception relations are inbound is a pure historical coincidence.
    return vertices.where(__.inE(getRelationNames()).otherV().is(P.within(otherResults)));
  }

  @Override
  public EntityRef createRef(Vertex vertex) {
    Edge inEdge = vertex.edges(Direction.IN, getRelationNames()).next();
    Vertex otherVertex = inEdge.outVertex();

    EntityRef targetRef = super.createRef(vertex);

    EntityRef sourceRef = otherSearch.getSearchDescription().createRef(otherVertex);

    EntityRef ref = new EntityRef("wwrelation", (String) inEdge.property("tim_id").value());

    ref.setRelationName(inEdge.label());
    ref.setTargetData(targetRef.getData());
    ref.setTargetName(targetRef.getDisplayName());
    ref.setSourceData(sourceRef.getData());
    ref.setSourceName(sourceRef.getDisplayName());

    return ref;
  }

}
