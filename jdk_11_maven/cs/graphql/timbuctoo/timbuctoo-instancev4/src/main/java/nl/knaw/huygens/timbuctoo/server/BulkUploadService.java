package nl.knaw.huygens.timbuctoo.server;

import nl.knaw.huygens.timbuctoo.bulkupload.InvalidFileException;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.Loader;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ResultReporter;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.StateMachine;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerpopSaver;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public class BulkUploadService {
  public static final Logger LOG = LoggerFactory.getLogger(BulkUploadService.class);
  private final Vres vres;
  private final TinkerPopGraphManager graphwrapper;
  private final int maxVertices;

  public BulkUploadService(Vres vres, TinkerPopGraphManager graphwrapper, int maxVerticesPerTransaction) {
    this.vres = vres;
    this.graphwrapper = graphwrapper;
    this.maxVertices = maxVerticesPerTransaction;
  }

  public void saveToDb(String vreName, Loader loader, List<Tuple<String, File>> tempFiles, String vreLabel,
                       Consumer<String> statusUpdate) throws IOException, InvalidFileException {

    String fileNamesDisplay;
    if (tempFiles.size() == 1) {
      fileNamesDisplay = tempFiles.get(0).getLeft();
    } else {
      fileNamesDisplay = "multiple files: " + tempFiles.stream().map(Tuple::getLeft).collect(joining(", "));
    }

    try (TinkerpopSaver saver =
           new TinkerpopSaver(vres, graphwrapper, vreName, vreLabel, maxVertices, fileNamesDisplay)) {
      try {
        loader.loadData(tempFiles, new Importer(new StateMachine(saver), new ResultReporter(statusUpdate)));
        saver.setUploadFinished(vreName, Vre.PublishState.MAPPING_CREATION);
      } catch (IOException | InvalidFileException e) {
        saver.setUploadFinished(vreName, Vre.PublishState.UPLOAD_FAILED);
        throw e;
      }
    }
  }
}
