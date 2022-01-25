package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.dataset.PatchRdfCreator;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PredicateMutationRdfPatcher implements PatchRdfCreator {
  private final List<PredicateMutation> mutations;

  public PredicateMutationRdfPatcher(PredicateMutation... mutations) {
    this.mutations = Lists.newArrayList();
    if (mutations != null) {
      this.mutations.addAll(Arrays.asList(mutations));
    }
  }


  @Override
  public void sendQuads(RdfPatchSerializer saver, Consumer<String> importStatusConsumer, DataSet dataSet)
      throws LogStorageFailedException {
    final QuadStore quadStore = dataSet.getQuadStore();
    final Map<String, String> foundSubjects = new HashMap<>();
    for (PredicateMutation mutation : mutations) {
      for (Map.Entry<UUID, PredicateMutation.SubjectFinder> entry : mutation.getSubjectFinders().entrySet()) {
        foundSubjects.put(entry.getKey().toString(), entry.getValue().getSubject(quadStore));
      }

      for (CursorQuad newValue : mutation.getFullRetractions()) {
        final String subject = foundSubjects.getOrDefault(newValue.getSubject(), newValue.getSubject());
        final String predicate = newValue.getPredicate();
        final Direction direction = newValue.getDirection();

        try (Stream<CursorQuad> quads = quadStore.getQuads(subject, predicate, direction, "")) {
          for (CursorQuad oldValue : (Iterable<CursorQuad>) quads::iterator) {
            saver.delQuad(
                oldValue.getSubject(),
                oldValue.getPredicate(),
                oldValue.getObject(),
                oldValue.getValuetype().orElse(null),
                oldValue.getLanguage().orElse(null),
                null
            );
          }
        }
      }

      for (CursorQuad oldValue : mutation.getRetractions()) {
        saver.delQuad(
            foundSubjects.getOrDefault(oldValue.getSubject(), oldValue.getSubject()),
            oldValue.getPredicate(),
            foundSubjects.getOrDefault(oldValue.getObject(), oldValue.getObject()),
            oldValue.getValuetype().orElse(null),
            oldValue.getLanguage().orElse(null),
            null
        );
      }

      for (CursorQuad newValue : mutation.getAdditions()) {
        if (newValue.getObject() != null) {
          saver.onQuad(
              foundSubjects.getOrDefault(newValue.getSubject(), newValue.getSubject()),
              newValue.getPredicate(),
              foundSubjects.getOrDefault(newValue.getObject(), newValue.getObject()),
              newValue.getValuetype().orElse(null),
              newValue.getLanguage().orElse(null),
              null
          );
        }
      }
    }
  }
}
