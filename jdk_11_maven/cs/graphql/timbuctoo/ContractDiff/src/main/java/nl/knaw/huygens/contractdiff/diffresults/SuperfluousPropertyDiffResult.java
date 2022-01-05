package nl.knaw.huygens.contractdiff.diffresults;

public class SuperfluousPropertyDiffResult extends DiffResult {
  private String value;

  public SuperfluousPropertyDiffResult(String value) {
    this.value = value;
  }

  @Override
  public String asHtml() {
    return String.format("<span class=\"superfluous\">%s" +
      "<span class=\"expected\"> //not part of contract</span></span>", escc(value));
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    return indent + String.format("<span class='superfluous'>%s%s%s" +
      "<span class=\"expected\"> //not part of contract</span></span>", escc(key), escc(value), delimiter);
  }

  @Override
  public String asConsole() {
    return String.format(SUBDUED +  "%s //not part of contract", value);
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    return String.format(indent +
        SUBDUED + "%s" + //key
        "%s" + //val
        "%s " + //delimiter
        "//not part of contract", //expected
      key, value, delimiter);
  }

  @Override
  public boolean wasSuccess() {
    return true;
  }
}
