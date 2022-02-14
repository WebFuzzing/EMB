package nl.knaw.huygens.concordion.extensions;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;

public class Utils {
  public static String getTextAndRemoveIndent(Element element) {
    String text = element.getText();
    StringBuilder prefix = new StringBuilder();
    for (char ch : text.toCharArray()) {
      if (ch == '\n' || ch == '\r') {
        prefix.delete(0, prefix.length());
        continue;
      }
      if (ch != ' ' && ch != '\t') {
        break;
      }

      prefix.append(ch);
    }

    return text.replace("\n" + prefix, "\n").trim();
  }

  public static String replaceVariableReferences(final Evaluator evaluator, final String body,
                                                 final ResultRecorder resultRecorder) {
    if (body == null) {
      return null;
    }
    StrSubstitutor sub = new StrSubstitutor(new StrLookup<Object>() {
      @Override
      public String lookup(String name) {
        try {
          Object value = evaluator.evaluate(name);
          if (value == null) {
            return "";
          } else {
            return value.toString();
          }
        } catch (Exception e) {
          resultRecorder.record(Result.FAILURE);
          return "<span class=\"failure\">" + e.toString() + "</span>";
        }
      }
    }, "$(", ")", '\\');

    return sub.replace(body);
  }

  static Element replaceWithEmptyElement(Element origElement, String name, String namespace, Element caption) {

    Element anchor;
    if (caption != null) {
      origElement.appendSister(caption);
      anchor = caption;
    } else {
      anchor = origElement;
    }

    Element resultElement = new Element(origElement.getLocalName());
    anchor.appendSister(resultElement);
    origElement.moveAttributesTo(resultElement);
    resultElement
      .removeAttribute(name, namespace);

    origElement.getParentElement().removeChild(origElement);
    return resultElement;
  }

  static void addClass(Element resultElement, String newClass) {
    String className = resultElement.getAttributeValue("class");
    resultElement
      .addAttribute("class", className == null ? newClass : className + " " + newClass);
  }
}
