package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import org.junit.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.PaginationHelper.getPaginatedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PaginationHelperTest {

  private static final Base64.Decoder DECODER = Base64.getDecoder();


  public static final Function<CursorQuad, SubjectReference> MAKE_ITEM = c -> new SubjectReference() {
    @Override
    public String getSubjectUri() {
      return c.getSubject();
    }

    @Override
    public Set<String> getTypes() {
      return new HashSet<>();
    }

    @Override
    public DataSet getDataSet() {
      return null;
    }
  };

  public CursorQuad getObject(String cursor, String subjectUri) {
    return CursorQuad.create(
      subjectUri,
      "",
      Direction.OUT,
      "",
      null,
      null,
      cursor
    );
  }

  @Test
  public void firstPageHasNullAsPrev() throws Exception {
    final PaginatedList<SubjectReference> paginatedList = getPaginatedList(
      Stream.of(
        getObject("c1", "http://example.org/1"),
        getObject("c2", "http://example.org/2"),
        getObject("c3", "http://example.org/3"),
        getObject("c4", "http://example.org/4")
      ),
      MAKE_ITEM,
      PaginationArguments.create(2, "", Optional.empty()),
      Optional.empty()
    );

    assertThat(decode(paginatedList.getNextCursor()), is("A\nc2"));
    assertThat(paginatedList.getPrevCursor(), is(Optional.empty()));
    assertThat(paginatedList.getItems().size(), is(2));
    assertThat(paginatedList.getItems().get(0).getSubjectUri(), is("http://example.org/1"));
    assertThat(paginatedList.getItems().get(1).getSubjectUri(), is("http://example.org/2"));
  }

  @Test
  public void secondPageHasPrevAndNext() throws Exception {
    final PaginatedList<SubjectReference> paginatedList = getPaginatedList(
      Stream.of(
        getObject("c3", "http://example.org/3"),
        getObject("c4", "http://example.org/4"),
        getObject("c5", "http://example.org/4")
      ),
      MAKE_ITEM,
      PaginationArguments.create(2, "A\nc2", Optional.empty()),
      Optional.empty()
    );

    assertThat(decode(paginatedList.getNextCursor()), is("A\nc4"));
    assertThat(decode(paginatedList.getPrevCursor()), is("D\nc3"));
    assertThat(paginatedList.getItems().size(), is(2));
    assertThat(paginatedList.getItems().get(0).getSubjectUri(), is("http://example.org/3"));
    assertThat(paginatedList.getItems().get(1).getSubjectUri(), is("http://example.org/4"));
  }

  @Test
  public void lastPageHasNullAsNext() throws Exception {
    final PaginatedList<SubjectReference> paginatedList = getPaginatedList(
      Stream.of(
        getObject("c4", "http://example.org/4"),
        getObject("c3", "http://example.org/3"),
        getObject("c2", "http://example.org/2"),
        getObject("c1", "http://example.org/1")
      ),
      MAKE_ITEM,
      PaginationArguments.create(2, "LAST", Optional.empty()),
      Optional.empty()
    );

    assertThat(paginatedList.getNextCursor(), is(Optional.empty()));
    assertThat(decode(paginatedList.getPrevCursor()), is("D\nc3"));
    assertThat(paginatedList.getItems().size(), is(2));

    //still list the items in ascending order
    assertThat(paginatedList.getItems().get(0).getSubjectUri(), is("http://example.org/3"));
    assertThat(paginatedList.getItems().get(1).getSubjectUri(), is("http://example.org/4"));
  }

  @Test
  public void downwardsToFirstPageIsSameAsStartingFromFirstPage() throws Exception {
    final PaginatedList<SubjectReference> paginatedList = getPaginatedList(
      Stream.of(
        getObject("c2", "http://example.org/2"),
        getObject("c1", "http://example.org/1")
      ),
      MAKE_ITEM,
      PaginationArguments.create(2, "D\nc3", Optional.empty()),
      Optional.empty()
    );

    assertThat(decode(paginatedList.getNextCursor()), is("A\nc2"));
    assertThat(decode(paginatedList.getPrevCursor()), is(nullValue()));
    assertThat(paginatedList.getItems().size(), is(2));
    assertThat(paginatedList.getItems().get(0).getSubjectUri(), is("http://example.org/1"));
    assertThat(paginatedList.getItems().get(1).getSubjectUri(), is("http://example.org/2"));
  }


  private String decode(Optional<String> encoded) {
    return encoded.map(s -> new String(DECODER.decode(s.getBytes()))).orElse(null);
  }
}
