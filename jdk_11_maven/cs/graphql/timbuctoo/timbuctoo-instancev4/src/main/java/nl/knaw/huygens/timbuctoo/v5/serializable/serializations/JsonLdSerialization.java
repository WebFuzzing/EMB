package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
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
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class JsonLdSerialization implements Serialization {
  private final JsonGenerator generator;
  protected final SerializeDispatcher dispatcher;

  public JsonLdSerialization(OutputStream outputStream) throws IOException {
    generator = new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8)
      .setPrettyPrinter(new DefaultPrettyPrinter());
    dispatcher = new SerializeDispatcher();
  }

  @Override
  public void serialize(SerializableResult serializableResult) throws IOException {
    final Set<PredicateInfo> context = new LinkedHashSet<>();
    generator.writeStartObject();
    generator.writeFieldName("data");
    generator.writeStartObject();
    for (Map.Entry<String, Serializable> entry : serializableResult.getData().getContents().entrySet()) {
      generator.writeFieldName(entry.getKey());
      dispatcher.dispatch(entry.getValue(), context);
    }
    generator.writeEndObject();
    writeContext(context);
    generator.writeEndObject();
    generator.flush();
    generator.close();
  }

  private void writeContext(Set<PredicateInfo> context) throws IOException {
    generator.writeFieldName("@context");
    generator.writeStartObject();
    //ignore the data wrapper by marking it as an index map and as the @graph container
    generator.writeFieldName("data");
    generator.writeStartObject();
    generator.writeStringField("@id", "@graph");
    generator.writeStringField("@container", "@index");
    generator.writeEndObject();
    generator.writeStringField("value", "@value");
    generator.writeStringField("type", "@type");
    for (PredicateInfo entry : context) {
      if (entry.getUri().isPresent()) {
        if (entry.getDirection() == Direction.IN) {
          generator.writeFieldName(entry.getSafeName());
          generator.writeStartObject();
          if (entry.getDirection() == Direction.IN) {
            generator.writeStringField("@reverse", entry.getUri().get());
          } else {
            generator.writeStringField("@id", entry.getUri().get());
          }
          generator.writeEndObject();
        } else {
          generator.writeStringField(entry.getSafeName(), entry.getUri().get());
        }
      } else {
        generator.writeNullField(entry.getSafeName());
      }
    }
    generator.writeEndObject();
  }

  private class SerializeDispatcher extends Dispatcher<Set<PredicateInfo>> {

    @Override
    public void handleEntity(Entity entity, Set<PredicateInfo> context) throws IOException {
      generator.writeStartObject();
      generator.writeStringField("@id", entity.getUri());
      generator.writeStringField("@type", entity.getTypeUri());
      for (Map.Entry<PredicateInfo, Serializable> entry : entity.getContents().entrySet()) {
        generator.writeFieldName(entry.getKey().getSafeName());
        context.add(entry.getKey());
        dispatch(entry.getValue(), context);
      }
      generator.writeEndObject();
    }

    @Override
    public void handleNull(Set<PredicateInfo> context) throws IOException {
      generator.writeNull();
    }

    @Override
    public void handleList(SerializableList list, Set<PredicateInfo> context) throws IOException {
      generator.writeStartObject();
      if (list.getPrevCursor().isPresent()) {
        generator.writeStringField("prevCursor", list.getPrevCursor().get());
      }
      if (list.getNextCursor().isPresent()) {
        generator.writeStringField("nextCursor", list.getNextCursor().get());
      }
      generator.writeFieldName("items");
      generator.writeStartArray();
      for (Serializable o : list.getItems()) {
        dispatch(o, context);
      }
      generator.writeEndArray();
      generator.writeEndObject();
    }

    @Override
    public void handleGraphqlObject(GraphqlIntrospectionObject object, Set<PredicateInfo> context) throws IOException {
      generator.writeStartObject();
      for (Map.Entry<String, Serializable> entry : object.getContents().entrySet()) {
        generator.writeFieldName(entry.getKey());
        dispatch(entry.getValue(), context);
      }
      generator.writeEndObject();
    }

    @Override
    public void handleGraphqlList(GraphqlIntrospectionList list, Set<PredicateInfo> context) throws IOException {
      generator.writeStartArray();
      for (Serializable o : list.getItems()) {
        dispatch(o, context);
      }
      generator.writeEndArray();
    }

    @Override
    public void handleGraphqlValue(GraphqlIntrospectionValue object, Set<PredicateInfo> context) throws IOException {
      if (object == null) {
        generator.writeNull();
      } else {
        generator.writeObject(object.getValue());
      }
    }

    @Override
    public void handleValue(Value value, Set<PredicateInfo> context) throws IOException {
      String type = RdfConstants.STRING;
      if (!value.getType().isEmpty()) {
        type = value.getType();
      }
      generator.writeStartObject();
      generator.writeStringField("type", type);
      generator.writeStringField("value", value.getValue());
      if (value.getGraphqlTypeName().isPresent()) {
        generator.writeStringField("__typename", value.getGraphqlTypeName().get());
      }
      generator.writeEndObject();
    }
  }
}
