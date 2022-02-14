package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;

import java.util.function.Consumer;

public class AddTriplePatchRdfCreator implements PatchRdfCreator {

  @JsonProperty
  private final String subject;
  @JsonProperty
  private final String predicate;
  @JsonProperty
  private final String object;
  @JsonProperty
  private final String dataType;

  @JsonCreator
  public AddTriplePatchRdfCreator(@JsonProperty("subject") String subject,
                                  @JsonProperty("predicate") String predicate,
                                  @JsonProperty("object") String object,
                                  @JsonProperty("dataType") String dataType) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
    this.dataType = dataType;
  }

  @Override
  public void sendQuads(RdfPatchSerializer saver, Consumer<String> importStatusConsumer,
                        DataSet dataSet) throws LogStorageFailedException {
    saver.onQuad(subject,predicate,object,dataType,null, null);
  }
}
