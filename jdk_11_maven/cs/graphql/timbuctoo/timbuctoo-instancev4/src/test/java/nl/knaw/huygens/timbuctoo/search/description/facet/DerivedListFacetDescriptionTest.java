package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.junit.Before;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

public class DerivedListFacetDescriptionTest {

  public static final String RELATION_NAME = "hasKeyword";
  public static final String FACET_NAME = "facetName";
  public static final String RELATION = "relation";
  public static final String PROPERTY = "property";
  public static final String VALUE1 = "value1";
  public static final String VALUE2 = "value2";
  public static final String RELATION_2 = "relation2";
  private PropertyParser parser;

  @Before
  public void setUp() throws Exception {
    parser = mock(PropertyParser.class);
    given(parser.parse(anyString())).willAnswer(invocation -> invocation.getArguments()[0]);
  }
}
