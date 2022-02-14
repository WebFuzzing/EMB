package nl.knaw.huygens.timbuctoo.graph;


import com.google.common.collect.Sets;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class NodeTest {

  private static final String TIM_ID = "the-tim-id";
  private static final String TYPE = "document";
  private static final String DOCUMENT_LABEL = "the title";
  private Vertex mockDocumentVertex;

  @Before
  public void setUp() {
    mockDocumentVertex = mock(Vertex.class);

    VertexProperty timIdProperty = mock(VertexProperty.class);
    VertexProperty typesProperty = mock(VertexProperty.class);

    given(timIdProperty.value()).willReturn(TIM_ID);
    given(typesProperty.value()).willReturn("[\"" + TYPE +  "\"]");

    given(mockDocumentVertex.property("tim_id")).willReturn(timIdProperty);
    given(mockDocumentVertex.property("types")).willReturn(typesProperty);
    given(mockDocumentVertex.value("document_title")).willReturn(DOCUMENT_LABEL);
    given(mockDocumentVertex.keys()).willReturn(Sets.newHashSet("document_title"));
  }

  @Test
  public void getLabelReturnsTheLabel() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.getLabel(), is(DOCUMENT_LABEL));
  }

  @Test
  public void getTypeReturnsTheType() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.getType(), is(TYPE));
  }

  @Test
  public void getKeyReturnsTheKey() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.getKey(), is(TYPE + "s/" + TIM_ID));
  }

  @Test
  public void equalsReturnsFalseWhenOtherIsNull() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.equals(null), is(false));
  }

  @Test
  public void equalsReturnsTrueWhenOtherHasTheSameMemoryReference() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.equals(underTest), is(true));
  }

  @Test
  public void equalsReturnsFalseWhenOtherIsNotANode() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);

    assertThat(underTest.equals(new Object()), is(false));

  }

  @Test
  public void equalsReturnsTrueWhenOtherNodeHasTheSameKey() throws IOException {
    Node underTest = new Node(mockDocumentVertex, null);
    Node toCompare = new Node(mockDocumentVertex, null);

    assertThat(underTest.equals(toCompare), is(true));
    assertThat(underTest == toCompare, is(false));
  }

  @Test
  public void nodeAlsoSupportsPersons() throws IOException {
    Vertex mockPersonVertex = mock(Vertex.class);
    VertexProperty timIdProperty = mock(VertexProperty.class);
    VertexProperty typesProperty = mock(VertexProperty.class);
    final String timId = "tim-id";
    final String type = "person";
    final String tempName = "the label";
    given(timIdProperty.value()).willReturn(timId);
    given(typesProperty.value()).willReturn("[\"" + type +  "\"]");
    given(mockPersonVertex.property("tim_id")).willReturn(timIdProperty);
    given(mockPersonVertex.property("types")).willReturn(typesProperty);
    given(mockPersonVertex.value("wwperson_tempName")).willReturn(tempName);
    given(mockPersonVertex.keys()).willReturn(Sets.newHashSet("wwperson_tempName"));

    Node underTest = new Node(mockPersonVertex, null);

    assertThat(underTest.getType(), is(type));
    assertThat(underTest.getKey(), is(type + "s/" + timId));
    assertThat(underTest.getLabel(), is(tempName));
  }

  @Test(expected = IOException.class)
  public void nodeConstructorThrowsIoExceptionWhenTypeIsNotSupported() throws IOException {
    Vertex mockUnsupportedVertex = mock(Vertex.class);
    VertexProperty typesProperty = mock(VertexProperty.class);
    VertexProperty timIdProperty = mock(VertexProperty.class);
    final String timId = "tim-id";
    final String type = "unsupported";
    given(mockUnsupportedVertex.property("tim_id")).willReturn(timIdProperty);
    given(mockUnsupportedVertex.property("types")).willReturn(typesProperty);
    given(timIdProperty.value()).willReturn(timId);
    given(typesProperty.value()).willReturn("[\"" + type +  "\"]");

    new Node(mockUnsupportedVertex, null);
  }
}
