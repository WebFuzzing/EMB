package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.bulkupload.savers.Saver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StateMachine<T> {
  private Set<Integer> idsToSkip = new HashSet<>();
  private ImportPropertyDescriptions propertyDescriptions;
  private final Saver<T> saver;
  private T currentCollection;
  private List<ImportProperty> currentProperties;

  private ImportState currentState = ImportState.NOTHING;

  public StateMachine(Saver<T> saver) {
    this.saver = saver;
  }

  public Result startCollection(String collectionName) {
    if (currentState != ImportState.NOTHING) {
      return Result.failure("I was not expecting a collection declaration here");
    }
    if (collectionName.equals("")) {
      return Result.failure("This collection has no name!");
    }
    currentCollection = saver.addCollection(collectionName);
    currentState = ImportState.GETTING_DECLARATION;
    propertyDescriptions = new ImportPropertyDescriptions();
    return Result.success();
  }

  public Result registerPropertyName(int id, String name) {
    if (idsToSkip.contains(id) || currentState == ImportState.SKIPPING) {
      return Result.ignored();
    }
    if (currentState != ImportState.GETTING_DECLARATION) {
      return Result.failure("I was not expecting a property declaration here");
    }
    propertyDescriptions.getOrCreate(id).setPropertyName(name);
    return Result.success();
  }

  public void startEntity() {
    if (currentState == ImportState.GETTING_DECLARATION) {
      currentState = ImportState.GETTING_VALUES;
      saver.addPropertyDescriptions(currentCollection, propertyDescriptions);
    }
    currentProperties = Lists.newArrayList();
  }

  public Result setValue(int id, String value) {
    if (currentState != ImportState.GETTING_VALUES) {
      return Result.failure("I was not expecting a value property here");
    }
    if (idsToSkip.contains(id) || currentState == ImportState.SKIPPING) {
      return Result.ignored();
    }
    Optional<ImportPropertyDescription> propOpt = propertyDescriptions.get(id);
    if (propOpt.isPresent()) {
      if (!Strings.isNullOrEmpty(value)) {
        ImportPropertyDescription prop = propOpt.get();
        currentProperties.add(new ImportProperty(prop, value));
      }
      return Result.ignored();//actual validation will happen during finishEntity
    } else {
      return Result.failure("No property declared for id '" + id + "', property value is '" + value + "'");
    }
  }

  public HashMap<Integer, Result> finishEntity() {
    HashMap<String, String> propertyValues = new HashMap<>();
    HashMap<Integer, Result> results = new HashMap<>();
    currentProperties.stream().filter(property -> property.getValue() != null).forEach(property -> {
      propertyValues.put(property.getName(), property.getValue());
      results.put(property.getId(), Result.success());
    });
    if (!propertyValues.isEmpty()) {
      saver.addEntity(currentCollection, propertyValues);
    }
    return results;
  }

  public void finishCollection() {
    currentState = ImportState.NOTHING;
    currentCollection = null;
  }

  private enum ImportState {
    NOTHING,
    GETTING_DECLARATION,
    GETTING_VALUES,
    SKIPPING
  }

}
