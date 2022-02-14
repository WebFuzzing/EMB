package nl.knaw.huygens.timbuctoo.databaselog.entry;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.databaselog.LogOutput;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class to make log entries for all the changed properties between two versions of a Vertex or an Edge.
 * This class is created, because both UpdateEdgeLogEntry and UpdateVertexLogEntry had the same functionality
 * regarding updating the properties.
 */
class PropertyUpdater {
  private final Element element;
  private final Element prevElement;
  private final Set<String> newKeys;
  private final Set<String> oldKeys;

  public PropertyUpdater(Element element, Element prevElement, Set<String> propertiesToIgnore) {
    this.element = element;
    this.prevElement = prevElement;
    this.newKeys = getKeys(element, propertiesToIgnore);
    this.oldKeys = getKeys(prevElement, propertiesToIgnore);
  }

  private Set<String> getKeys(Element element, Set<String> propertiesToIgnore) {
    return element.keys().stream().filter(key -> !propertiesToIgnore.contains(key)).collect(Collectors.toSet());
  }

  private void addNewProperties(LogOutput dbLog) {
    Set<String> newProperties = Sets.difference(newKeys, oldKeys);
    newProperties.forEach(key -> dbLog.newProperty(element.property(key)));
  }

  private void updateExistingProperties(LogOutput dbLog) {
    Set<String> existing = Sets.intersection(newKeys, oldKeys);
    existing.forEach(key -> {
      Property<Object> latestProperty = element.property(key);
      if (!Objects.equals(latestProperty.value(), prevElement.value(key))) {
        dbLog.updateProperty(latestProperty);
      }
    });
  }

  private void removeDeletedProperties(LogOutput dbLog) {
    Set<String> deletedProperties = Sets.difference(oldKeys, newKeys);
    deletedProperties.forEach(dbLog::deleteProperty);
  }

  public void updateProperties(LogOutput dbLog) {
    addNewProperties(dbLog);
    updateExistingProperties(dbLog);
    removeDeletedProperties(dbLog);
  }
}
