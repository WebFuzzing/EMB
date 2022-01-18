package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfBlankNode;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class RrTemplate implements RrTermMap {
  private final String template;
  private final TermType termType;
  private final String dataType;
  private final Pattern pattern;
  private static final Logger LOG = getLogger(RrTemplate.class);

  public RrTemplate(String template, TermType termType, String dataType) {
    this.template = template;
    this.termType = termType;
    this.dataType = dataType;
    //regex can be tested by going to https://regex101.com/r/fV1zJ1/1
    //It has been tested with
    // http://jan/{} <- should not match
    // http://jan/{foo}
    // http://jan/{fo\}o}
    // http://jan/{fo\}o\\}
    // http://jan/{fo\}o\\\}}
    // http://jan/{fo\}o\\\\}} <- should mark before-last curly as the end of the group
    // http://jan/{foo}/{bar}
    // http://jan/{foo}/{bar}?really=\{as} <- should not match as
    // http://jan/{foo}/{bar}?really=\\{as} <- should match as

    //Explanation:
    //  (?<!\\)     Negative Lookbehind: will make the regex fizzle if preceded by \ (\\ to escape it)
    //  (?:\\\\)*   (?:) is a non-matching group (we group, but the result does not end up in the matches)
    //              (?:\\\\)* matches any even number (or zero) of backslashes
    //  \{( ... )\} Here the real matching starts with the literal { immediately after it we open the matching group (we
    //              want whatever's inside the curly's)
    //  (?: ... )+  We're now going to repeatedly (at least once) encounter any of three options
    //
    //  [^\\\}]     a character that is not { or \
    //  (?:(?<!\\)(?:\\\\)+) an even number of slashes not preceded by a slash
    //  \\\}        the exact combination \}
    //
    // nothing matches an odd number of slashes so \\} is matched in two steps: first \\ is matched as "an even number
    // of slashes" then the } is matched as the end of the regex. On the other hand \\\} is has the first two slashes
    // matched by the even number of slashes matcher and the remaining \} matched by the "exact combination \}" matcher
    pattern = Pattern.compile(
      "(?<!\\\\)(?:\\\\\\\\)*\\{((?:[^\\\\\\}]|(?:(?<!\\\\)(?:\\\\\\\\)+)|(?:\\\\\\})+)+)\\}"
    );
  }

  @Override
  public Optional<QuadPart> generateValue(Row input) {
    Matcher regexMatcher = pattern.matcher(template);
    StringBuffer resultString = new StringBuffer();
    while (regexMatcher.find()) {
      String value = input.getRawValue(regexMatcher.group(1));
      if (value == null) {
        return Optional.empty();
      } else {
        if (termType == TermType.IRI) {
          try {
            value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
          } catch (UnsupportedEncodingException e) {
            LOG.error("Java is being really stoopid and throws an unsupportedEncodingException for UTF-8.");
            throw new RuntimeException(e);
          }
        }
        regexMatcher.appendReplacement(resultString, value);
      }
    }

    if (StringUtils.isBlank(resultString.toString())) {
      return Optional.empty();
    }

    regexMatcher.appendTail(resultString);

    switch (termType) {
      case IRI:
        return Optional.of(new RdfUri(resultString.toString()));
      case BlankNode:
        return Optional.of(new RdfBlankNode(resultString.toString()));
      case Literal:
        return Optional.of(new RdfValue(resultString.toString(), dataType));
      default:
        throw new UnsupportedOperationException("Not all items in the Enumerable where handled");
    }

  }

  @Override
  public String toString() {
    Matcher regexMatcher = pattern.matcher(template);
    StringBuffer resultString = new StringBuffer();
    while (regexMatcher.find()) {
      regexMatcher.appendReplacement(resultString, "«" + regexMatcher.group(1) + "»");
    }
    regexMatcher.appendTail(resultString);

    return String.format("      Template: %s (%s)\n",
      resultString,
      this.termType
    );
  }

}
