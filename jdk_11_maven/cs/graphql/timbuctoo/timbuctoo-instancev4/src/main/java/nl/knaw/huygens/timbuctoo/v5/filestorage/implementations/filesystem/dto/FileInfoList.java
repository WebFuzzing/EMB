package nl.knaw.huygens.timbuctoo.v5.filestorage.implementations.filesystem.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableFileInfoList.class)
@JsonDeserialize(as = ImmutableFileInfoList.class)
public interface FileInfoList {
  static FileInfoList create(Map<String, FileInfo> items) {
    return ImmutableFileInfoList.builder().items(items).build();
  }

  static FileInfoList create() {
    return ImmutableFileInfoList.builder().build();
  }

  default FileInfoList addItem(String token, FileInfo item) {
    return ImmutableFileInfoList.builder().from(this).putItems(token, item).build();
  }

  Map<String, FileInfo> getItems();
}
