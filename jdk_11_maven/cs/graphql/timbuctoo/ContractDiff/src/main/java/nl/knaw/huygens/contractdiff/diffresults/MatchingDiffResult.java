package nl.knaw.huygens.contractdiff.diffresults;

public class MatchingDiffResult extends DiffResult {
  public final String expected;
  public final String val;

  public MatchingDiffResult(String expected, String val) {
    this.expected = expected;
    this.val = val;
  }

  @Override
  public String asHtml() {
    return String.format("<span class=\"matching\"><span class=\"val\">%s</span> " +
      "<span class=\"expected\">//%s</span></span>", escc(val), escc(expected));
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    return indent + String.format("<span class=\"matching\">%s<span class=\"val\">%s</span>%s " +
      "<span class=\"expected\">//is %s</span></span>", escc(key), escc(val), delimiter, escc(expected));
  }

  @Override
  public String asConsole() {
    return String.format(GOOD +  "%s " + NORMAL + "//is %s", val, expected);
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    return String.format(indent + "%s" + //key
      GOOD +  "%s" +  //val
        NORMAL + "%s //is %s", //delimiter & expected

      key, val, delimiter, expected);
  }

  @Override
  public boolean wasSuccess() {
    return true;
  }
}
