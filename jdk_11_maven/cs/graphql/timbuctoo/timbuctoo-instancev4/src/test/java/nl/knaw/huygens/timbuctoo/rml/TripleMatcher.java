package nl.knaw.huygens.timbuctoo.rml;

import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class TripleMatcher extends TypeSafeDiagnosingMatcher<Quad> {

  private final QuadPart subject;
  private final QuadPart predicate;
  private final QuadPart object;

  public static QuadPart ANY = new RdfUri("An always matching QuadPart");

  public TripleMatcher(QuadPart subject, QuadPart predicate, QuadPart object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  public static TripleMatcher likeTriple(QuadPart subject, QuadPart predicate, QuadPart object) {
    return new TripleMatcher(subject, predicate, object);
  }

  @Override
  protected boolean matchesSafely(Quad actual, Description mismatchDescription) {
    boolean subjectMatches = subject == ANY || subject.equals(actual.getSubject());
    mismatchDescription.appendText("Triple with");
    if (!subjectMatches) {
      mismatchDescription.appendText(" Subject with value ").appendValue(actual.getSubject());
    }

    boolean predicateMatches = predicate == ANY || predicate.equals(actual.getPredicate());
    if (!predicateMatches) {
      mismatchDescription.appendText(" Predicate with value ").appendValue(actual.getSubject());
    }

    boolean objectMatches = object == ANY || object.equals(actual.getObject());
    if (!objectMatches) {
      mismatchDescription.appendText(" Object with value ").appendValue(actual.getObject());
    }

    return subjectMatches && predicateMatches && objectMatches;
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue(subject).appendText(" ")
               .appendValue(predicate).appendText(" ")
               .appendValue(object).appendText(" .");
  }

}
