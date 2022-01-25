package nl.knaw.huygens.timbuctoo.v5.serializable.serializations.base;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;
import nl.knaw.huygens.timbuctoo.v5.serializable.Serialization;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Entity;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionObject;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionValue;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.PredicateInfo;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Serializable;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.SerializableList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Value;
import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.Dispatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CollectionsOfEntitiesSerialization implements Serialization {

  protected Map<String, Map<String, AggregatedEntity>> allEntities;

  public CollectionsOfEntitiesSerialization() {
    allEntities = new HashMap<>();
  }

  @Override
  public void serialize(SerializableResult serializableResult) throws IOException {
    CollectDistinctEntities collector = new CollectDistinctEntities();
    for (Serializable serializable : serializableResult.getData().getContents().values()) {
      collector.dispatch(serializable, null);
    }
  }

  public AggregatedEntity getEntity(Entity entity) {
    return allEntities
      .computeIfAbsent(entity.getTypeUri(), key -> new HashMap<>())
      .computeIfAbsent(entity.getUri(), key -> new AggregatedEntity());
  }

  private class CollectDistinctEntities extends Dispatcher<Setter> {

    @Override
    public void handleEntity(Entity entity, Setter context)
      throws IOException {
      if (context != null) {
        context.addRelation(entity.getUri());
      }

      AggregatedEntity target = getEntity(entity);
      for (Map.Entry<PredicateInfo, Serializable> entry : entity.getContents().entrySet()) {
        if (entry.getKey().getUri().isPresent()) { //only use actual rdf data, don't serialize graphql properties
          String predicateUri = entry.getKey().getUri().get();
          Serializable value = entry.getValue();
          if (entry.getKey().getDirection() == Direction.IN) {
            if (value instanceof Entity) {
              getEntity((Entity) value).relations
                .computeIfAbsent(predicateUri, k -> new HashSet<>())
                  .add(entity.getUri());
            } else if (value instanceof SerializableList) {
              for (Serializable item : ((SerializableList) value).getItems()) {
                if (item instanceof Entity) {
                  getEntity((Entity) item).relations
                    .computeIfAbsent(predicateUri, k -> new HashSet<>())
                      .add(entity.getUri());
                }
              }
            }
            dispatch(value, null); //don't add the subEntity to this entry
          } else {
            dispatch(value, new Setter(target, predicateUri));
          }
        }
      }
    }

    @Override
    public void handleList(SerializableList list, Setter context) throws IOException {
      for (Serializable item : list.getItems()) {
        dispatch(item, context);
      }
    }

    @Override
    public void handleValue(Value object, Setter setter) throws IOException {
      setter.addAttribute(object);
    }

    @Override
    public void handleGraphqlObject(GraphqlIntrospectionObject object, Setter context) throws IOException {
      for (Serializable item : object.getContents().values()) {
        dispatch(item, context);
      }
    }

    @Override
    public void handleGraphqlList(GraphqlIntrospectionList list, Setter context) throws IOException {
      for (Serializable item : list.getItems()) {
        dispatch(item, context);
      }
    }

    @Override
    public void handleGraphqlValue(GraphqlIntrospectionValue object, Setter context) throws IOException { }

    @Override
    public void handleNull(Setter context) throws IOException { }

  }

  public class AggregatedEntity {
    public final Map<String, Set<Value>> attributes = new HashMap<>();
    public final Map<String, Set<String>> relations = new HashMap<>();
  }

  private class Setter {
    private final AggregatedEntity entity;
    private final String predicateUri;

    public Setter(AggregatedEntity entity, String predicateUri) {
      this.entity = entity;
      this.predicateUri = predicateUri;
    }

    public void addAttribute(Value value) {
      entity.attributes.computeIfAbsent(predicateUri, k -> new HashSet<>()).add(value);
    }

    public void addRelation(String uri) {
      entity.relations.computeIfAbsent(predicateUri, k -> new HashSet<>()).add(uri);
    }
  }
}
