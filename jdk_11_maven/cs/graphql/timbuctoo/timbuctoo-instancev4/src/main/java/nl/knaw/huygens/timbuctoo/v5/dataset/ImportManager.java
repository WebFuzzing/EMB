package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.RdfCreator;
import nl.knaw.huygens.timbuctoo.v5.filestorage.FileStorage;
import nl.knaw.huygens.timbuctoo.v5.filestorage.LogStorage;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedFile;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.FileStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonDataStore;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfParser;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import org.slf4j.Logger;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * - provides methods for adding data (files and rdf). Will persist that data
 * - makes sure that the derived stores are kept in sync
 */
public class ImportManager implements DataProvider {
  private static final Logger LOG = getLogger(ImportManager.class);
  private final FileStorage fileStorage;
  private final FileStorage imageStorage;
  private final LogStorage logStorage;
  private final RdfIoFactory serializerFactory;
  private final ExecutorService executorService;
  private final JsonDataStore<LogList> logListStore;
  private final List<RdfProcessor> subscribedProcessors;
  private final Runnable webhooks;
  private final ImportStatus importStatus;
  private final ArrayList<Runnable> importSucceededListeners;
  private DataSet dataSet;

  public ImportManager(JsonDataStore<LogList> logListStore, FileStorage fileStorage, FileStorage imageStorage,
                       LogStorage logStorage, ExecutorService executorService, RdfIoFactory rdfIoFactory,
                       Runnable onUpdated) {
    this.webhooks = onUpdated;
    this.fileStorage = fileStorage;
    this.imageStorage = imageStorage;
    this.logStorage = logStorage;
    this.serializerFactory = rdfIoFactory;
    this.executorService = executorService;
    this.logListStore = logListStore;
    subscribedProcessors = new ArrayList<>();
    importStatus = new ImportStatus(this.logListStore.getData());
    importSucceededListeners = Lists.newArrayList();
  }

  public Future<ImportStatus> addLog(String baseUri, String defaultGraph, String fileName,
                                                   InputStream rdfInputStream,
                                                   Optional<Charset> charset, MediaType mediaType)
    throws LogStorageFailedException {

    importStatus.start(this.getClass().getSimpleName() + ".addLog", baseUri);
    int[] index = new int[1];
    try {
      String token = logStorage.saveLog(rdfInputStream, fileName, mediaType, charset);
      logListStore.updateData(logList -> {
        index[0] = logList.addEntry(LogEntry.create(baseUri, defaultGraph, token));
        return logList;
      });
    } catch (IOException e) {
      importStatus.addError("Could not save log", e);
      throw new LogStorageFailedException(e);
    }
    return executorService.submit(() -> processLogsUntil(index[0]));
  }

  public String addFile(InputStream fileStream, String fileName, MediaType mediaType)
    throws FileStorageFailedException {
    try {
      return fileStorage.saveFile(fileStream, fileName, mediaType);
    } catch (IOException e) {
      throw new FileStorageFailedException(e);
    }
  }

  public CachedFile getFile(String fileToken) throws IOException {
    Optional<CachedFile> maybeCacheFile = fileStorage.getFile(fileToken);
    if (maybeCacheFile.isPresent()) {
      return maybeCacheFile.get();
    } else {
      throw new IOException("No cached file for token " + fileToken);
    }
  }

  public String addImage(InputStream imageStream, String imageName, MediaType mediaType)
    throws FileStorageFailedException {
    try {
      return imageStorage.saveFile(imageStream, imageName, mediaType);
    } catch (IOException e) {
      throw new FileStorageFailedException(e);
    }
  }

  public Future<ImportStatus> generateLog(String baseUri, String defaultGraph, RdfCreator creator)
    throws LogStorageFailedException {

    importStatus.start(this.getClass().getSimpleName() + ".generateLog", baseUri);
    try {
      //add to the log structure
      int[] index = new int[1];
      logListStore.updateData(logList -> {
        index[0] = logList.addEntry(LogEntry.create(baseUri, defaultGraph, creator));
        return logList;
      });
      //schedule processing
      return executorService.submit(() -> processLogsUntil(index[0]));
    } catch (IOException e) {
      importStatus.addError("Could not update logList", e);
      throw new LogStorageFailedException(e);
    }
  }

  public Future<ImportStatus> reprocessLogs() {
    try {
      logListStore.updateData(logList -> {
        logList.markAsProcessed(-1);
        return logList;
      });
    } catch (IOException e) {
      LOG.error("Could not reset log list processed", e);
    }
    return processLogs();
  }

  public Future<ImportStatus> processLogs() {
    return executorService.submit(() -> processLogsUntil(Integer.MAX_VALUE));
  }

  private synchronized ImportStatus processLogsUntil(int maxIndex) {
    importStatus.start(this.getClass().getSimpleName() + ".processLogs", null);
    ListIterator<LogEntry> unprocessed = logListStore.getData().getUnprocessed();
    boolean dataWasAdded = false;
    while (unprocessed.hasNext() && unprocessed.nextIndex() <= maxIndex) {
      int index = unprocessed.nextIndex();
      LogEntry entry = unprocessed.next();
      importStatus.startEntry(entry);
      if (!entry.getLogToken().isPresent()) { // no logToken, because the data is generated from a tabular file
        RdfCreator creator = entry.getRdfCreator().get();
        String token = "";
        MediaType mediaType;
        Optional<Charset> charset;

        File tempFile = null;
        try {
          tempFile = File.createTempFile("log_to_generate", "nq");
          try (OutputStream stream = new GZIPOutputStream(new FileOutputStream(tempFile))) {
            if (creator instanceof PlainRdfCreator) {
              try (RdfSerializer serializer = serializerFactory.makeRdfSerializer(stream)) {
                mediaType = serializer.getMediaType();
                charset = Optional.of(serializer.getCharset());
                ((PlainRdfCreator) creator).sendQuads(serializer, dataSet, importStatus::setStatus);
              } catch (Exception e) {
                LOG.error("Log generation failed", e);
                importStatus.addError("Log generation failed", e);
                break;
              }
            } else {
              try (RdfPatchSerializer srlzr = serializerFactory.makeRdfPatchSerializer(stream, entry.getBaseUri())) {
                mediaType = srlzr.getMediaType();
                charset = Optional.of(srlzr.getCharset());
                ((PatchRdfCreator) creator).sendQuads(srlzr, importStatus::setStatus, dataSet);
              } catch (Exception e) {
                LOG.error("Log generation failed", e);
                importStatus.addError("Log generation failed", e);
                break;
              }
            }
          }

          try (InputStream inputStream = new GZIPInputStream(new FileInputStream(tempFile))) {
            token = logStorage.saveLog(
              inputStream,
              "log_generated_by_" + creator.getClass().getSimpleName(),
              mediaType,
              charset
            );
          }
          entry = LogEntry.addLogToEntry(entry, token);
          unprocessed.set(entry); //maybe use loglist.setLog or something like that

          token = "";
        } catch (Exception e) {
          if (token.isEmpty()) {
            LOG.error("Log processing failed", e);
          } else {
            LOG.error("Log processing failed. Log created but not added to the list!", e);
          }
          importStatus.addError("Log processing failed", e);
          break;
        } finally {
          if (tempFile != null) {
            tempFile.delete();
          }
        }
      }

      String logToken = entry.getLogToken().get();
      try (CachedLog log = logStorage.getLog(logToken)) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        for (RdfProcessor processor : subscribedProcessors) {
          if (processor.getCurrentVersion() <= index) {
            String msg = "******* " + processor.getClass().getSimpleName() + " Started importing full log...";
            LOG.info(msg);
            importStatus.setStatus(msg);
            importStatus.setEntryName(log.getName());
            RdfParser rdfParser = serializerFactory.makeRdfParser(log);
            processor.start(index);
            rdfParser.importRdf(log, entry.getBaseUri(), entry.getDefaultGraph(), processor);
            processor.commit();
          }
        }
        long elapsedTime = stopwatch.elapsed(TimeUnit.SECONDS);
        String msg = "Finished importing. Total import took " + elapsedTime + " seconds.";
        LOG.info(msg);
        importStatus.setStatus(msg);
        dataWasAdded = true;
      } catch (Exception e) {
        LOG.error("Failed for file: {}", entry.getLogToken().get());
        LOG.error("Processing log failed", e);
        importStatus.addError("Processing log failed", e);
      }

      // Update the log, even after RdfProcessingFailedException | IOException
      try {
        logListStore.updateData(logList -> {
          logList.markAsProcessed(index);
          return logList;
        });
      } catch (IOException e) {
        LOG.error("Updating the log failed", e);
        importStatus.addError("Updating log failed", e);
      }
      importStatus.finishEntry();
    } // end main while loop
    if (dataWasAdded) {
      webhooks.run();
      importSucceededListeners.forEach(Runnable::run);
    }
    importStatus.finishList();
    // update log.json
    try {
      logListStore.updateData(Function.identity());
    } catch (IOException e) {
      LOG.error("Updating the log failed", e);
      importStatus.addError("Updating log failed", e);
    }
    return importStatus;
  }

  @Override
  public void subscribeToRdf(RdfProcessor processor) {
    subscribedProcessors.add(processor);
  }

  public boolean isRdfTypeSupported(MediaType mediaType) {
    return serializerFactory.isRdfTypeSupported(mediaType);
  }

  List<LogEntry> getLogEntries() {
    return logListStore.getData().getEntries();
  }

  public LogList getLogList() {
    return logListStore.getData();
  }

  public synchronized ImportStatus getImportStatus() {
    return importStatus;
  }

  public void init(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public void subscribeImportSucceeded(Runnable importSucceededListener) {
    this.importSucceededListeners.add(importSucceededListener);
  }
}
