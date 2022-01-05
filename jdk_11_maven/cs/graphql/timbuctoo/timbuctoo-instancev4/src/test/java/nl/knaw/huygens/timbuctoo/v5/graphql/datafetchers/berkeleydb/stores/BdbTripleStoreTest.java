package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.stores;

import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTripleStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad.create;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.LANGSTRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;


public class BdbTripleStoreTest {


  public static final String EX = "http://example.org/";
  protected BdbNonPersistentEnvironmentCreator databaseCreator;
  protected BdbTripleStore tripleStore;

  @Before
  public void makeCollection() throws Exception {
    databaseCreator = new BdbNonPersistentEnvironmentCreator();
    tripleStore = new BdbTripleStore(
      databaseCreator.getDatabase(
        "userId",
        "dataSetId",
        "rdfData",
        true,
        TupleBinding.getPrimitiveBinding(String.class),
        TupleBinding.getPrimitiveBinding(String.class),
        new StringStringIsCleanHandler()
      )
    );
    Thread.sleep(2000); // to make the test work on slow systems
  }

  @After
  public void cleanUp() throws Exception {
    databaseCreator.close();
  }

  @Test
  public void returnsTheData() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "12", "http://number", null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en");
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "Gauthier", LANGSTRING, "FR-fr");

    try (Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "")) {
      List<CursorQuad> resultList = quads.collect(toList());
      assertThat(resultList, contains(
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, "12", "http://number", null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en", ""),
        create(EX + "subject1", "http://pred", Direction.OUT, "Gauthier", LANGSTRING, "FR-fr", "")
      ));
    }
  }

  @Test
  public void canIterateForward() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null);

    try (Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "")) {
      List<CursorQuad> resultList = quads.collect(toList());
      assertThat(resultList, contains(
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null, "")
      ));
    }
  }

  @Test
  public void canIterateBackward() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null);

    try (Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "LAST")) {
      List<CursorQuad> resultList = quads.collect(toList());
      assertThat(resultList, contains(
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null, "")
      ));
    }
  }

  @Test
  public void canIterateForwardFromCursor() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null);

    String cursor;
    try (Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "")) {
      //get the first two items and the cursor of the last one
      cursor = quads.limit(2).reduce((first, second) -> second).orElse(null).getCursor();
    }
    try (Stream<CursorQuad> quads =
           tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "A\n" + cursor)) {
      List<CursorQuad> resultList = quads.collect(toList());
      assertThat(resultList, contains(
        //starting from the cursor and going ascending should give us the last two
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null, ""),
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null, "")
      ));
    }
  }


  @Test
  public void canIterateBackwardFromCursor() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject3", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject4", null, null);

    String cursor;
    try (Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "")) {
      //get the first two items and the cursor of the last one
      cursor = quads.limit(2).reduce((first, second) -> second).orElse(null).getCursor();
    }
    try (Stream<CursorQuad> quads =
           tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "D\n" + cursor)) {
      List<CursorQuad> resultList = quads.collect(toList());
      assertThat(resultList, contains(
        //starting from the cursor and going descending should give us the first
        create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null, "")
      ));
    }
  }

  @Test
  public void relationQuadRetractionRemovesTheQuadFromTheStore() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.deleteQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);

    Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "");
    assertThat(quads.collect(toList()), not(hasItem(
      create(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null, "http://some graph")
    )));
    quads.close();
  }



  @Test
  public void relationQuadRetractionRemovesTheReverseRelationQuadFromTheStore() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject1", null, null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);
    tripleStore.deleteQuad(EX + "subject1", "http://pred", Direction.OUT, EX + "subject2", null, null);

    Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject2", "http://pred_inverse", Direction.OUT, "");
    assertThat(quads.collect(toList()), not(hasItem(
      create(EX + "subject2", "http://pred_inverse", Direction.OUT, EX + "subject1", null, null, "http://some graph")
    )));
    quads.close();
  }

  @Test
  public void langStringQuadRetractionQuadRemovesTheQuadFromTheStore() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en");
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "Gauthier", LANGSTRING, "FR-fr");
    tripleStore.deleteQuad(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en");

    Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "");
    assertThat(quads.collect(toList()), not(hasItem(
      create(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en", "http://some graph")
    )));
    quads.close();
  }

  @Test
  public void valueQuadRetractionQuadRemovesTheQuadFromTheStore() throws Exception {
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "12", "http://number", null);
    tripleStore.putQuad(EX + "subject1", "http://pred", Direction.OUT, "14", "http://number", null);
    tripleStore.deleteQuad(EX + "subject1", "http://pred", Direction.OUT, "12", "http://number", null);

    Stream<CursorQuad> quads = tripleStore.getQuads(EX + "subject1", "http://pred", Direction.OUT, "");
    assertThat(quads.collect(toList()), not(hasItem(
      create(EX + "subject1", "http://pred", Direction.OUT, "Walter", LANGSTRING, "EN-en", "http://some graph")
    )));
    quads.close();
  }

}
