package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTruePatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.UpdatedPerPatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangesRetriever {

  private BdbTruePatchStore bdbTruePatchStore;
  private UpdatedPerPatchStore updatedPerPatchStore;

  public ChangesRetriever(BdbTruePatchStore bdbTruePatchStore, UpdatedPerPatchStore updatedPerPatchStore) {
    this.bdbTruePatchStore = bdbTruePatchStore;
    this.updatedPerPatchStore = updatedPerPatchStore;
  }

  public Supplier<List<Integer>> getVersions() {
    return () -> {
      try (Stream<Integer> versions = updatedPerPatchStore.getVersions()) {
        return versions.collect(Collectors.toList());
      }
    };
  }

  public Stream<CursorQuad> retrieveChanges(Integer version) {
    return Stream.concat(
      bdbTruePatchStore.getChangesOfVersion(version, false),
      bdbTruePatchStore.getChangesOfVersion(version, true)
    ).filter(quad -> quad.getDirection().equals(Direction.OUT));
  }

  public boolean versionExists(Integer requestedVersion) {
    try (Stream<Integer> versions = updatedPerPatchStore.getVersions()) {
      return versions.anyMatch(version -> version.equals(requestedVersion));
    }
  }
}

