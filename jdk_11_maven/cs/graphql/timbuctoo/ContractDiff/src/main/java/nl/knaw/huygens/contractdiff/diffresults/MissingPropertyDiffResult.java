package nl.knaw.huygens.contractdiff.diffresults;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MissingPropertyDiffResult extends DiffResult {
  private String value;

  public MissingPropertyDiffResult(String value) {
    this.value = value;
  }

  @Override
  public String asHtml() {
    return String.format("<span class=\"missing\">%s<span class=\"expected\"> //missing</span></span>", escc(value));
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    ObjectMapper mapper = new ObjectMapper();

    return indent + String.format("<span class='missing'>%s%s%s" +
      "<span class=\"expected\"> //missing</span></span>", escc(key), escc(value), delimiter);
  }

  @Override
  public String asConsole() {
    return String.format(BAD +  "%s " + NORMAL + "//missing", value);
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    return String.format(indent + BAD + "%s" + //key
        "%s" +  //val
        NORMAL + "%s " + //delimiter
        "//missing", //expected
      key, value, delimiter);
  }

  @Override
  public boolean wasSuccess() {
    return false;
  }
}
