package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel;


public class ProgressStep {
  private final String label;
  private final ImportStatusLabel status;
  private final String progress;
  private final String speed;

  public ProgressStep(String label, ImportStatusLabel status, String progress, String speed) {
    this.label = label;
    this.status = status;
    this.progress = progress;
    this.speed = speed;
  }

  @JsonIgnore
  static ProgressStep create(String label, ImportStatusLabel status, String progress, String speed) {
    return new ProgressStep(label, status, progress, speed);
    // return ImmutableImportStatusProgress.builder().label(label).status(status).progress(progress).speed(speed).
    // build();
  }

  public String getLabel() {
    return label;
  }

  public ImportStatusLabel getStatus() {
    return status;
  }

  public String getProgress() {
    return progress;
  }

  public String getSpeed() {
    return speed;
  }
}
