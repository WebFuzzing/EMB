package nl.knaw.huygens.timbuctoo.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class LinkTest {

  public static final String RELATION_NAME = "relationName";
  private Edge edgeMock;
  private Edge compareEdgeMockDifferentType;

  @Before
  public void setUp() {
    edgeMock = mock(Edge.class);
    given(edgeMock.label()).willReturn(RELATION_NAME);
    compareEdgeMockDifferentType = mock(Edge.class);
    given(compareEdgeMockDifferentType.label()).willReturn("somethingElse");
  }

  @Test
  public void getSourceReturnsTheSource() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.getSource(), is(2));
  }

  @Test
  public void getTargetReturnsTheTarget() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.getTarget(), is(1));
  }

  @Test
  public void getTypeReturnsTheType() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.getType(), is(RELATION_NAME));

  }

  @Test
  public void equalsReturnsFalseWhenOtherIsNull() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.equals(null), is(false));
  }

  @Test
  public void equalsReturnsTrueWhenOtherHasTheSameMemoryReference() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.equals(underTest), is(true));
  }

  @Test
  public void equalsReturnsFalseWhenOtherIsNotALink() {
    Link underTest = new Link(edgeMock, 2, 1);

    assertThat(underTest.equals(new Object()), is(false));
  }

  @Test
  public void equalsReturnsFalseWhenOtherLinkDoesNotMatchType() {
    Link underTest = new Link(edgeMock, 2, 1);
    Link compareTo = new Link(compareEdgeMockDifferentType, 2, 1);


    assertThat(underTest.equals(compareTo), is(false));
  }

  @Test
  public void equalsReturnsFalseWhenOtherLinkHasDifferentSourceTargetCombination() {
    Link underTest = new Link(edgeMock, 2, 1);
    Link compareTo = new Link(edgeMock, 3, 1);


    assertThat(underTest.equals(compareTo), is(false));
  }


  @Test
  public void equalsReturnsTrueWhenOtherLinkHasTheSameTypeAndSourceTargetCombination() {
    Link underTest = new Link(edgeMock, 2, 1);
    Link compareTo = new Link(edgeMock, 1, 2);
    Link compareTo2 = new Link(edgeMock, 2, 1);

    assertThat(underTest.equals(compareTo), is(true));
    assertThat(underTest.equals(compareTo2), is(true));
    assertThat(compareTo.equals(compareTo2), is(true));
  }
}
