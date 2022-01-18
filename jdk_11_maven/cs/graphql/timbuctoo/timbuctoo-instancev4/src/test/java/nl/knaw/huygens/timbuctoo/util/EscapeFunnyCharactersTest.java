package nl.knaw.huygens.timbuctoo.util;

import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.util.EscapeFunnyCharacters.escapeFunnyCharacters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EscapeFunnyCharactersTest {
  @Test
  public void escapeFunnyCharactersWorks() throws Exception {
    assertThat(escapeFunnyCharacters("A B"), is("A_32B"));
    assertThat(escapeFunnyCharacters("A B_/"), is("A_32B___47"));
  }

}
