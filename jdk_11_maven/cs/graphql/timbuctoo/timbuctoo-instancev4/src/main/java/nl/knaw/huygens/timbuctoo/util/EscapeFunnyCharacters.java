package nl.knaw.huygens.timbuctoo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscapeFunnyCharacters {
  public static String escapeFunnyCharacters(String vreId) {
    StringBuffer resultString = new StringBuffer();
    Pattern regex = Pattern.compile("[^a-zA-Z0-9_-]");
    Matcher regexMatcher = regex.matcher(vreId.replaceAll("_", "__"));
    while (regexMatcher.find()) {
      // You can vary the replacement text for each match on-the-fly
      String cur = regexMatcher.group();
      if (cur.length() > 1) {
        throw new RuntimeException("The world is not as I expected it to be!");
      }
      regexMatcher.appendReplacement(resultString, "_" + cur.codePointAt(0) + "");
    }
    regexMatcher.appendTail(resultString);
    return resultString.toString();
  }
}
