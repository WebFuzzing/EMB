package nl.knaw.huygens.timbuctoo.graph;

import com.google.common.collect.Sets;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;

public class D3GraphTest {

  private Edge mockEdge(String relationName) {
    Edge edgeMock = mock(Edge.class);
    given(edgeMock.label()).willReturn(relationName);
    return edgeMock;
  }

  private Vertex mockVertex(String timId, String type, String labelPropertyKey, String label) {
    Vertex mockDocumentVertex = mock(Vertex.class);

    VertexProperty timIdProperty = mock(VertexProperty.class);
    VertexProperty typesProperty = mock(VertexProperty.class);

    given(timIdProperty.value()).willReturn(timId);
    given(typesProperty.value()).willReturn("[\"" + type +  "\"]");

    given(mockDocumentVertex.property("tim_id")).willReturn(timIdProperty);
    given(mockDocumentVertex.property("types")).willReturn(typesProperty);
    given(mockDocumentVertex.value(labelPropertyKey)).willReturn(label);
    given(mockDocumentVertex.keys()).willReturn(Sets.newHashSet(labelPropertyKey));
    return mockDocumentVertex;
  }

  @Test
  public void addNodeAddsNodeForVertexWhenNodesDoesNotContainTheVertexAsANode() throws IOException {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");
    Vertex mockVertex2 = mockVertex("2", "document", "document_title", "title2");

    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex2, null);

    assertThat(underTest.getNodes(), contains(
            new Node(mockVertex1, null),
            new Node(mockVertex2, null)
    ));
  }

  @Test
  public void addNodeDoesNotAddNodeForVertexWhenNodesDoesContainTheVertexAsANode() throws IOException {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");

    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex1, null);

    assertThat(underTest.getNodes(), contains(
            new Node(mockVertex1, null)
    ));
  }

  @Test
  public void addNodeDoesNotAddNodeForVertexWhenVertexTypeIsNotSupported() throws IOException {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");
    Vertex mockVertex2 = mockVertex("2", "unsupported", "document_title", "title2");

    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex2, null);

    assertThat(underTest.getNodes(), contains(
            new Node(mockVertex1, null)
    ));
  }

  @Test
  public void addLinkAddsLinkForEdgeWhenLinksDoesNotContainTheEdgeAsALinkAndSetsTheCorrectIndexForSourceAndTarget() {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");
    Vertex mockVertex2 = mockVertex("2", "document", "document_title", "title2");
    Vertex mockVertex3 = mockVertex("3", "document", "document_title", "title3");

    Edge mockEdge = mockEdge("someRelation");
    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex2, null);
    underTest.addNode(mockVertex3, null);

    underTest.addLink(mockEdge, mockVertex1, mockVertex2, null, null);
    underTest.addLink(mockEdge, mockVertex1, mockVertex3, null, null);

    assertThat(underTest.getLinks(), contains(
            new Link(mockEdge, 0, 1),
            new Link(mockEdge, 0, 2)
    ));
  }

  @Test
  public void addLinkDoesNotAddLinkForEdgeWhenLinksDoesTheEdgeAsALink() {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");
    Vertex mockVertex2 = mockVertex("2", "document", "document_title", "title2");

    Edge mockEdge = mockEdge("someRelation");
    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex2, null);

    underTest.addLink(mockEdge, mockVertex1, mockVertex2, null,
      null);
    underTest.addLink(mockEdge, mockVertex2, mockVertex1, null,
      null);

    assertThat(underTest.getLinks(), contains(
            new Link(mockEdge, 0, 1)
    ));
  }

  @Test
  public void addLinkDoesNotAddLinkForEdgeWhenEitherVertexTypeIsNotSupported() {
    D3Graph underTest = new D3Graph();
    Vertex mockVertex1 = mockVertex("1", "document", "document_title", "title1");
    Vertex mockVertex2 = mockVertex("2", "document", "document_title", "title2");
    Vertex mockVertex3 = mockVertex("3", "unsupported", "document_title", "title3");

    Edge mockEdge = mockEdge("someRelation");
    underTest.addNode(mockVertex1, null);
    underTest.addNode(mockVertex2, null);
    underTest.addNode(mockVertex3, null);

    underTest.addLink(mockEdge, mockVertex1, mockVertex2, null,
      null);
    underTest.addLink(mockEdge, mockVertex1, mockVertex3, null,
      null);
    underTest.addLink(mockEdge, mockVertex3, mockVertex1, null,
      null);


    assertThat(underTest.getLinks(), contains(
            new Link(mockEdge, 0, 1)
    ));
  }

}
