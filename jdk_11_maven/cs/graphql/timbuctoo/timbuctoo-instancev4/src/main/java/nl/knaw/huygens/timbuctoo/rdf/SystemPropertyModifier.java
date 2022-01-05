package nl.knaw.huygens.timbuctoo.rdf;

import com.google.common.collect.Lists;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.databind.node.JsonNodeFactory;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

/**
 * FIXME: deduplicate code in TinkerpopJsonCrudService
 */
public class SystemPropertyModifier {

  private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
  private final Clock clock;

  public SystemPropertyModifier(Clock clock) {
    this.clock = clock;
  }

  private String getFormattedUpdateString(String userId) {
    return String.format("{\"timeStamp\":%s,\"userId\":%s}",
        clock.millis(),
        nodeFactory.textNode(userId)
      );
  }

  private String getFormattedUpdateString(String userId, String vreId) {
    return String.format("{\"timeStamp\":%s,\"userId\":%s,\"vreId\":%s}",
      clock.millis(),
      nodeFactory.textNode(userId),
      nodeFactory.textNode(vreId)
    );
  }

  public void setCreated(Element element, String userId) {
    String value = getFormattedUpdateString(userId);
    element.property("created", value);
    element.property("modified", value);
  }

  public void setCreated(Element element, String userId, String vreId) {
    String value = getFormattedUpdateString(userId, vreId);
    element.property("created", value);
    element.property("modified", value);
  }

  public void setModified(Element element, String userId) {
    String value = getFormattedUpdateString(userId);
    element.property("modified", value);
  }

  public void setTimId(Element element) {
    element.property("tim_id", UUID.randomUUID().toString());
  }

  public void setTimId(Element element, String timId) {
    element.property("tim_id", timId);
  }

  public void setRev(Element element, int rev) {
    element.property("rev", rev);
  }

  public void setIsLatest(Element element, boolean isLatest) {
    element.property("isLatest", isLatest);
  }

  public void setIsDeleted(Element element, boolean isDeleted) {
    element.property("deleted", isDeleted);
  }

}
