package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BdbTypeNameStoreTest {

  @Test
  public void test() throws IOException {
    BdbTypeNameStore store = new BdbTypeNameStore(
      new DataStorage() {
        String value;
        @Override
        public String getValue() {
          return value;
        }

        @Override
        public void setValue(String newValue) throws DatabaseWriteException {
          value = newValue;
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public void commit() {

        }

        @Override
        public void beginTransaction() {

        }

        @Override
        public boolean isClean() {
          return true;
        }

        @Override
        public void empty() {

        }
      },
      "http://example.org"
    );
    store.addPrefix("_", "http://example.com/underscore#");
    store.addPrefix("foo", "http://example.com/foo#");

    String graphQlname = store.makeGraphQlname("http://example.com/underscore#test");

    assertThat(graphQlname, is("__test"));
  }

  @Test
  public void translatesListPredicatesBackToUri() throws Exception {
    BdbTypeNameStore store = new BdbTypeNameStore(
      new DataStorage() {
        String value;
        @Override
        public String getValue() {
          return value;
        }

        @Override
        public void setValue(String newValue) throws DatabaseWriteException {
          value = newValue;
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public void commit() {

        }

        @Override
        public void beginTransaction() {

        }

        @Override
        public boolean isClean() {
          return true;
        }

        @Override
        public void empty() {

        }
      },
      "http://example.org"
    );

    store.addPrefix("foo", "http://example.com/foo#");
    String originalUri = "http://example.com/foo#test";
    String predicate = store.makeGraphQlnameForPredicate(originalUri, Direction.OUT, true);

    assertThat(predicate, is("foo_testList"));
    assertThat(Optional.of(Tuple.tuple(originalUri, Direction.OUT)), is(store.makeUriForPredicate(predicate)));
  }

}
