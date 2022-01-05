package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ImportPropertyDescriptions implements Iterable<ImportPropertyDescription> {
  private HashMap<Integer, ImportPropertyDescription> propertyDescs = new HashMap<>();
  private List<ImportPropertyDescription> ordered = new ArrayList<>();

  public ImportPropertyDescriptions() {
  }

  public Optional<ImportPropertyDescription> get(int id) {
    if (propertyDescs.containsKey(id)) {
      return Optional.of(propertyDescs.get(id));
    } else {
      return Optional.empty();
    }
  }

  public ImportPropertyDescription getByOrder(int order) {
    return ordered.get(order);
  }


  public ImportPropertyDescription getOrCreate(int id) {
    if (propertyDescs.containsKey(id)) {
      return propertyDescs.get(id);
    } else {
      ImportPropertyDescription desc = new ImportPropertyDescription(id, ordered.size());
      ordered.add(desc);
      propertyDescs.put(id, desc);
      return desc;
    }
  }

  public int getPropertyCount() {
    return ordered.size();
  }

  @Override
  public Iterator<ImportPropertyDescription> iterator() {
    return ordered.iterator();
  }

  @Override
  public void forEach(Consumer<? super ImportPropertyDescription> action) {
    ordered.forEach(action);
  }

  @Override
  public Spliterator<ImportPropertyDescription> spliterator() {
    return ordered.spliterator();
  }
}
