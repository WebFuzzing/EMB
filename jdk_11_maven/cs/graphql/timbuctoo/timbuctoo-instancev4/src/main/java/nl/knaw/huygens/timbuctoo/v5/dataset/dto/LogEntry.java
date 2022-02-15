package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableLogEntry.class)
@JsonDeserialize(as = ImmutableLogEntry.class)
public interface LogEntry {
  String getBaseUri();

  String getDefaultGraph();

  Optional<RdfCreator> getRdfCreator();

  Optional<String> getLogToken();

  EntryImportStatus getImportStatus();

  static LogEntry create(String baseUri, String defaultGraph, String token) {
    return ImmutableLogEntry.builder()
      .baseUri(baseUri)
      .defaultGraph(defaultGraph)
      .logToken(token)
      .rdfCreator(Optional.empty())
      .importStatus(new EntryImportStatus())
      .build();
  }

  static LogEntry create(String baseUri, String defaultGraph, RdfCreator creator) {
    return ImmutableLogEntry.builder()
      .baseUri(baseUri)
      .defaultGraph(defaultGraph)
      .logToken(Optional.empty())
      .rdfCreator(creator)
      .importStatus(new EntryImportStatus())
      .build();
  }

  static LogEntry addLogToEntry(LogEntry entry, String token) {
    return ImmutableLogEntry
      .copyOf(entry)
      .withRdfCreator(Optional.empty())
      .withLogToken(token);
  }

}
