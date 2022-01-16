package nl.knaw.huygens.concordion.extensions;

import nu.xom.Element;
import nu.xom.Elements;
import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.util.IOUtil;

public class ReplaceEmbeddedStylesheetExtension implements ConcordionExtension {

  private final String styleToRemove;
  private final String styleToReplace;

  public ReplaceEmbeddedStylesheetExtension() {
    styleToRemove = IOUtil.readResourceAsString("/org/concordion/internal/resource/embedded.css");
    styleToReplace = null;
  }

  public ReplaceEmbeddedStylesheetExtension(String styleToReplace) {
    this.styleToReplace = styleToReplace;
    styleToRemove = IOUtil.readResourceAsString("/org/concordion/internal/resource/embedded.css");
  }

  @Override
  public void addTo(ConcordionExtender concordionExtender) {
    if (styleToReplace != null) {
      concordionExtender.withLinkedCSS(styleToReplace, new Resource("/concordion.css"));
    }
    concordionExtender.withDocumentParsingListener(document -> {
      Element html = document.getRootElement();
      Element head = html.getFirstChildElement("head");
      if (head != null) {
        Elements styles = head.getChildElements("style");
        for (int i = 0; i < styles.size(); i++) {
          Element style = styles.get(i);
          if (styleToRemove.equals(style.getValue())) {
            head.removeChild(style);
          }
        }
      }
    });
  }
}
