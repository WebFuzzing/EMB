package nl.knaw.huygens.timbuctoo.v5.serializable.dto;

import com.google.common.collect.Lists;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true) //Needed to allow nulls in the collection
public interface SerializableList extends RdfData {
  Optional<String> getPrevCursor();

  Optional<String> getNextCursor();

  @AllowNulls
  List<Serializable> getItems();

  static SerializableList serializableList(String prevCur, String nextCur, Iterable<? extends Serializable> data) {
    return ImmutableSerializableList.builder()
      .prevCursor(Optional.ofNullable(prevCur))
      .nextCursor(Optional.ofNullable(nextCur))
      .items(data)
      .build();
  }

  static SerializableList serializableList(String prevCursor, String nextCursor, Serializable... data) {
    return serializableList(prevCursor, nextCursor, Lists.newArrayList(data));
  }
}
