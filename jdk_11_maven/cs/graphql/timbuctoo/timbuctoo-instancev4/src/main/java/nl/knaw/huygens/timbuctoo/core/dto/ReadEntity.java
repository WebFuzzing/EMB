package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.immutables.value.Value;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value.Immutable
public interface ReadEntity {
  Iterable<TimProperty<?>> getProperties();

  int getRev();

  boolean getDeleted();

  String getPid();

  URI getRdfUri();

  List<String> getTypes();

  Change getModified();

  Change getCreated();

  /**
   * @return The list of relationships in which this entity participates as the source.
   */
  List<RelationRef> getRelations();

  String getDisplayName();

  UUID getId();

  Map<String, Object> getExtraProperties();

  List<String> getRdfAlternatives();
}
