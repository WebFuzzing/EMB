package nl.knaw.huygens.contractdiff.diffresults;

import com.google.common.xml.XmlEscapers;

public abstract class DiffResult {

  protected static final String RESET = "\u001B[0m";
  protected static final String GOOD = "\u001B[32m";
  protected static final String BAD = "\u001B[31m";
  protected static final String SUBDUED = "\u001B[37m";
  protected static final String NORMAL = "\u001B[30m";

  public abstract String asHtml();

  public abstract String asHtml(String key, String indent, String delimiter);

  public abstract String asConsole();

  public abstract String asConsole(String key, String indent, String delimiter);

  public String asConsoleAnsiStripped() {
    return asConsole().replaceAll("\u001B\\[[;\\d]*m", ""); //replace color codes
  }

  public abstract boolean wasSuccess();

  //public abstract boolean isMatching();

  protected String xhtmlEscapeContent(String val) {
    return XmlEscapers.xmlContentEscaper().escape(val);
  }

  protected String escc(String val) {
    return xhtmlEscapeContent(val);
  }

  protected String xhtmlEscapeAttribute(String val) {
    return XmlEscapers.xmlAttributeEscaper().escape(val);
  }

  protected String esca(String val) {
    return xhtmlEscapeAttribute(val);
  }

}
