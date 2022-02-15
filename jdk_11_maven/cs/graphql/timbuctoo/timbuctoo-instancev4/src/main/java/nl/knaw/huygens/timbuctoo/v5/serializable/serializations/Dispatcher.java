package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Entity;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionObject;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionValue;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Serializable;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.SerializableList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Value;

import java.io.IOException;

public abstract class Dispatcher<T> {

  public abstract void handleEntity(Entity entity, T context) throws IOException;

  public abstract void handleNull(T context) throws IOException;

  public abstract void handleList(SerializableList list, T context) throws IOException;

  public abstract void handleGraphqlObject(GraphqlIntrospectionObject object, T context) throws IOException;

  public abstract void handleGraphqlList(GraphqlIntrospectionList list, T context) throws IOException;

  public abstract void handleGraphqlValue(GraphqlIntrospectionValue object, T context) throws IOException;

  public abstract void handleValue(Value object, T context) throws IOException;

  public void dispatch(Serializable entry, T context) throws IOException {
    if (entry instanceof Entity) {
      handleEntity((Entity) entry, context);
    } else if (entry instanceof SerializableList) {
      handleList((SerializableList) entry, context);
    } else if (entry instanceof Value) {
      handleValue((Value) entry, context);
    } else if (entry instanceof GraphqlIntrospectionObject) {
      handleGraphqlObject((GraphqlIntrospectionObject) entry, context);
    } else if (entry instanceof GraphqlIntrospectionList) {
      handleGraphqlList((GraphqlIntrospectionList) entry, context);
    } else if (entry instanceof GraphqlIntrospectionValue) {
      handleGraphqlValue((GraphqlIntrospectionValue) entry, context);
    } else if (entry  == null) {
      handleNull(context);
    } else {
      throw new IllegalStateException("Unknown value type " + entry);
    }
  }
}
