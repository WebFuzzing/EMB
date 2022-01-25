package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.timbuctoo.util.FileHelpers;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedFile;
import nl.knaw.huygens.timbuctoo.v5.filestorage.implementations.filesystem.FileSystemFileStorage;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonFileBackedData;
import nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j.Rdf4jIoFactory;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ImportManagerTest {

  protected File logListLocation;
  protected ImportManager importManager;
  protected File filesDir;
  protected FileSystemFileStorage fileStorage;

  @Before
  public void makeSimpleDataSet() throws IOException {
    logListLocation = File.createTempFile("logList", ".json");
    logListLocation.delete();
    filesDir = Files.createTempDir();
    fileStorage = new FileSystemFileStorage(filesDir);
    this.importManager = new ImportManager(
      JsonFileBackedData.getOrCreate(logListLocation, LogList::new, new TypeReference<LogList>() {
      }),
      fileStorage,
      fileStorage,
      fileStorage,
      Executors.newSingleThreadExecutor(),
      new Rdf4jIoFactory(),
      () -> { }
    );
  }

  @After
  public void cleanUp() throws IOException {
    logListLocation.delete();
    FileUtils.cleanDirectory(filesDir);
  }

  @Test
  public void addLogSavesTheLogToDisk() throws Exception {
    File file = FileHelpers.getFileFromResource(ImportManagerTest.class, "clusius.ttl").toFile();
    String name = "http://example.com/clusius.ttl";
    String defaultGraph = "http://example.com/defaultGraph";
    String baseUri = "http://example.com/baseUri";
    Future<ImportStatus> promise = importManager.addLog(
      baseUri,
      defaultGraph,
      name,
      new FileInputStream(file),
      Optional.of(Charsets.UTF_8),
      MediaType.valueOf("text/turtle")
    );

    ImportStatus status = promise.get();
    assertThat(status.getErrorCount(), is((0)));

    LogEntry logEntry = importManager.getLogEntries().get(0);
    assertThat(logEntry.getBaseUri(), is(baseUri));
    assertThat(logEntry.getDefaultGraph(), is(defaultGraph));
    //The first character is an @. if we can read that we apparently can access the file
    assertThat(fileStorage.getLog(logEntry.getLogToken().get()).getReader().read(), is(64));
  }

  @Test
  public void callsStoresWhenANewLogIsAdded() throws Exception {
    File file = FileHelpers.getFileFromResource(ImportManagerTest.class, "clusius.ttl").toFile();
    String name = "http://example.com/clusius.ttl";
    String defaultGraph = "http://example.com/defaultGraph";
    String baseUri = "http://example.com/baseUri";
    CountingProcessor processor = new CountingProcessor();
    importManager.subscribeToRdf(processor);


    Future<ImportStatus> promise = importManager.addLog(
      baseUri,
      defaultGraph,
      name,
      new FileInputStream(file),
      Optional.of(Charsets.UTF_8),
      MediaType.valueOf("text/turtle")
    );
    ImportStatus status = promise.get();
    assertThat(processor.getCounter(), is(28));
    assertThat(status.hasErrors(), is(false));
  }

  @Test
  public void generateLogSavesTheLogAndCallsTheStores() throws Exception {
    String defaultGraph = "http://example.com/defaultGraph";
    String baseUri = "http://example.com/baseUri";
    CountingProcessor processor = new CountingProcessor();
    importManager.subscribeToRdf(processor);

    Future<ImportStatus> promise = importManager.generateLog(
      baseUri,
      defaultGraph,
      new DummyRdfCreator()
    );

    ImportStatus status = promise.get();
    assertThat(status.hasErrors(), is(false));
    assertThat(processor.getCounter(), is(3));
    LogEntry logEntry = importManager.getLogEntries().get(0);
    assertThat(logEntry.getBaseUri(), is(baseUri));
    assertThat(logEntry.getDefaultGraph(), is(defaultGraph));
    //The first character is an < (start of a uri in nquads) if we can read that we apparently can access the file
    assertThat(fileStorage.getLog(logEntry.getLogToken().get()).getReader().read(), is(60));
  }

  private static class CachedFileMatcher extends CompositeMatcher<CachedFile> {
    private CachedFileMatcher() {

    }

    public static CachedFileMatcher cachedFile() {
      return new CachedFileMatcher();
    }

    private static CachedFileMatcher cachedFile(CachedFile cachedFile) {
      return cachedFile()
        .withFile(cachedFile.getFile())
        .withMimeType(cachedFile.getMimeType())
        .withName(cachedFile.getName());
    }

    public CachedFileMatcher withFile(File file) {
      this.addMatcher(new PropertyEqualityMatcher<CachedFile, File>("file", file) {
        @Override
        protected File getItemValue(CachedFile item) {
          return item.getFile();
        }
      });
      return this;
    }

    public CachedFileMatcher withName(String name) {
      this.addMatcher(new PropertyEqualityMatcher<CachedFile, String>("name", name) {
        @Override
        protected String getItemValue(CachedFile item) {
          return item.getName();
        }
      });
      return this;
    }

    public CachedFileMatcher withMimeType(MediaType mimeType) {
      this.addMatcher(new PropertyEqualityMatcher<CachedFile, MediaType>("mimeType", mimeType) {
        @Override
        protected MediaType getItemValue(CachedFile item) {
          return item.getMimeType();
        }
      });
      return this;
    }


  }


  private static class CountingProcessor implements RdfProcessor {
    private final AtomicInteger counter;
    private int currentVersion = -1;

    public CountingProcessor() {
      counter = new AtomicInteger();
    }

    private int getCounter() {
      return counter.get();
    }


    @Override
    public void setPrefix(String prefix, String iri) throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void addRelation(String subject, String predicate, String object, String graph)
      throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void addValue(String subject, String predicate, String value, String dataType, String graph)
      throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void addLanguageTaggedString(String subject, String predicate, String value,
                                        String language,
                                        String graph) throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void delRelation(String subject, String predicate, String object, String graph)
      throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void delValue(String subject, String predicate, String value, String valueType,
                         String graph)
      throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void delLanguageTaggedString(String subject, String predicate, String value,
                                        String language,
                                        String graph) throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }

    @Override
    public void start(int index) throws RdfProcessingFailedException {
      currentVersion = index;
      counter.incrementAndGet();
    }

    @Override
    public int getCurrentVersion() {
      return currentVersion;
    }

    @Override
    public void commit() throws RdfProcessingFailedException {
      counter.incrementAndGet();
    }
  }

}
