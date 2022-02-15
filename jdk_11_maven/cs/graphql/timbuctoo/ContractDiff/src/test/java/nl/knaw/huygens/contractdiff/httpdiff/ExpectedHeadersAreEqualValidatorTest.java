package nl.knaw.huygens.contractdiff.httpdiff;

import com.google.common.collect.LinkedListMultimap;
import org.junit.Test;

import static nl.knaw.huygens.contractdiff.httpdiff.ExpectedHeadersAreEqualValidator.validate;

public class ExpectedHeadersAreEqualValidatorTest {

  private LinkedListMultimap<String, String> multimap(String... contents) {
    LinkedListMultimap<String, String> result = LinkedListMultimap.create(contents.length / 2);
    for (int i = 0; i < contents.length; i += 2) {
      result.put(contents[i], contents[i+1]);
    }
    return result;
  }

  @Test
  public void SameHeader() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", "value"),
      multimap("header1", "value")
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void DifferingHeader() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", "value"),
      multimap("header1", "other")
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void DuplicateHeaderThatMatches() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", "value"),
      multimap(
        "header1", "value",
        "header1", "othervalue"
      )
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void DuplicateHeaderThatDoesNotMatch() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", "value"),
      multimap(
        "header1", "other",
        "header1", "othervalue"
      )
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void SuperFluousHeader() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", "value"),
      multimap(
        "header1", "value",
        "header2", "othervalue"
      )
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void emptyHeader() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", ""),
      multimap(
        "header1", "value"
      )
    );

    System.out.print(result.asConsole());
  }

  @Test
  public void missingHeader() {
    HttpHeadersDiffResult result = validate(
      multimap("header1", ""),
      multimap()
    );

    System.out.print(result.asConsole());
  }

}