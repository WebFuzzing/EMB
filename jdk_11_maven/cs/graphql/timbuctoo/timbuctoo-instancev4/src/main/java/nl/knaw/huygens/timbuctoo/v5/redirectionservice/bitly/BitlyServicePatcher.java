package nl.knaw.huygens.timbuctoo.v5.redirectionservice.bitly;

import nl.knaw.huygens.timbuctoo.v5.dataset.PatchRdfCreator;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;

import java.util.function.Consumer;

public class BitlyServicePatcher implements PatchRdfCreator {
  private String subject;
  private String predicate;
  private String object;
  private String dataType;

  public BitlyServicePatcher(String subject, String predicate, String object, String dataType) {
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
