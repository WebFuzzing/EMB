package nl.knaw.huygens.contractdiff.diffresults;

public class MisMatchDiffResult extends DiffResult {
  public final String expected;
  public final String val;

  public MisMatchDiffResult(String expected, String val) {
    this.expected = expected;
    this.val = val;
  }

  @Override
  public String asHtml() {
    return String.format("<span class=\"mismatch\"><span class=\"val\">%s</span>, " +
      "<span class=\"expected\">//expected %s</span></span>", escc(val), escc(expected));
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    return indent + String.format("<span class=\"mismatch\">%s<span class=\"val\">%s</span>%s " +
      "<span class=\"expected\">//expected %s</span></span>", escc(key), escc(val), delimiter, escc(expected));
  }

  @Override
  public String asConsole() {
    return String.format(BAD +  "%s " + NORMAL + "//expected %s", val, expected);
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    return String.format(indent + "%s" + //key
      BAD +  "%s" +  //val
      NORMAL + "%s //expected %s", //delimiter & expected
      key, val, delimiter, expected);
  }

  @Override
  public boolean wasSuccess() {
    return false;
  }
}
