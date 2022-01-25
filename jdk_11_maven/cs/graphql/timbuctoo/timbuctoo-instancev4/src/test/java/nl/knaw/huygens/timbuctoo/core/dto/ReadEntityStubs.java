package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO move to database.dto test package
public class ReadEntityStubs {
  public static ReadEntity readEntityWithDisplayNameIdAndRev(String displayName, UUID id, int rev) {
    return new ReadEntity() {
      @Override
      public Iterable<TimProperty<?>> getProperties() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public int getRev() {
        return rev;
      }

      @Override
      public boolean getDeleted() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public String getPid() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public URI getRdfUri() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public List<String> getTypes() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public Change getModified() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public Change getCreated() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public List<RelationRef> getRelations() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public String getDisplayName() {
        return displayName;
      }

      @Override
      public UUID getId() {
        return id;
      }

      @Override
      public Map<String, Object> getExtraProperties() {
        throw new UnsupportedOperationException("Not implemented yet");
      }

      @Override
      public List<String> getRdfAlternatives() {
        throw new UnsupportedOperationException("Not implemented yet");
      }
    };
  }
}
