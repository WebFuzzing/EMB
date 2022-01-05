package nl.knaw.huygens.timbuctoo.model;

import io.vavr.Value;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.arrayToEncodedArray;

//Most database access should probably be done through the tinkerpopjsoncrudservice
public class GraphReadUtils {

  public static Optional<Try<String[]>> getEntityTypes(Element element) {
    return getProp(element, "types", String.class)
      .map(encodedEntityTypes ->
        Try.of(() -> arrayToEncodedArray.tinkerpopToJava(encodedEntityTypes, String[].class)
      ));
  }

  public static String[] getEntityTypesOrDefault(Element element) {
    return getEntityTypes(element).flatMap(Value::toJavaOptional).orElse(new String[]{});
  }

  public static <V> Optional<V> getProp(final Element vertex, final String key, Class<? extends V> clazz) {
    try {
      Iterator<? extends Property<Object>> revProp = vertex.properties(key);
      if (revProp.hasNext()) {
        return Optional.of(clazz.cast(revProp.next().value()));
      } else {
        return Optional.empty();
      }
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }

  public static Optional<Collection> getCollectionByVreId(Element element, Vres mappings, String vreId) {

    final List<Collection> filteredTypes = Arrays.asList(getEntityTypesOrDefault(element))
      .stream()
      .map(type -> mappings.getCollectionForType(type).get())
      .filter(collection -> collection.getVreName().equals(vreId))
      .collect(toList());

    return Optional.ofNullable(filteredTypes.size() > 0 ? filteredTypes.get(0) : null);
  }


}
