package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.DataStream;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TinkerPopGetCollection implements DataStream<ReadEntity> {
  private final Stream<ReadEntity> entities;

  public TinkerPopGetCollection(Stream<ReadEntity> entities) {
    this.entities = entities;
  }

  @Override
  public <U> List<U> map(Function<ReadEntity, U> mapping) {
    List<U> collect = entities.map(mapping).collect(Collectors.toList());
    return collect;
  }

}
