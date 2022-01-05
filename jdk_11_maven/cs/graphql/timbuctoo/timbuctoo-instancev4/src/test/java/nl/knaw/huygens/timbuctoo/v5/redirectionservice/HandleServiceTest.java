package nl.knaw.huygens.timbuctoo.v5.redirectionservice;


import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportManager;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonDataStore;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueSender;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HandleServiceTest {
  @Test
  public void savePidGeneratesPersistedIdSuccessfully() throws Exception {
    DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet  = mock(DataSet.class);
    DataSetMetaData dataSetMetaData = createMockDataSetMetaData();
    given(dataSet.getMetadata()).willReturn(dataSetMetaData);

    RdfPatchSerializer saver = createMockRdfPatchSerializer();
    RdfIoFactory rdfIoFactory = createMockRdfIoFactory(saver);

    JsonDataStore<LogList> logListStore = new LogListJsonDataStore();
    ExecutorService executor = Executors.newFixedThreadPool(10);

    ImportManager importManager = new ImportManager(
      logListStore,
      null,
      null,
      null,
      executor,
      rdfIoFactory,
      null
    );
    given(dataSet.getImportManager()).willReturn(importManager);

    User user = User.create("testDisplayName", "testDataSetId");
    given(dataSetRepository.getDataSet(user, "testOwnerId", "testDataSetId"))
      .willReturn(Optional.of(dataSet));

    QueueManager queueManager = mock(QueueManager.class);
    QueueCreator queueCreator = new NonQueueQueueCreator();
    given(queueManager.createQueue(any(),anyString())).willReturn(queueCreator);

    PersistenceManager persistenceManager = createMockPersistenceManager();

    final HandleService handleService = new HandleService(persistenceManager, queueManager, dataSetRepository);
    EntityLookup entityLookup = createMockEntityLookup(user);
    RedirectionServiceParameters redirectionServiceParameters = new RedirectionServiceParameters(
      URI.create("redirectionUri"),
      entityLookup
    );

    handleService.savePid(redirectionServiceParameters);

    verify(saver).onQuad(
      "testEntityUri",
      RdfConstants.timPredicate("persistentUri"),
      "testPersistentUrl",
      RdfConstants.STRING,
      null,
      null
    );
  }

  private RdfPatchSerializer createMockRdfPatchSerializer() {
    RdfPatchSerializer saver = mock(RdfPatchSerializer.class);
    given(saver.getCharset()).willReturn(Charset.defaultCharset());
    return saver;
  }

  private RdfIoFactory createMockRdfIoFactory(RdfPatchSerializer saver) {
    RdfIoFactory rdfIoFactory = mock(RdfIoFactory.class);
    given(rdfIoFactory.makeRdfPatchSerializer(any(),anyString())).willReturn(saver);
    return rdfIoFactory;
  }

  private PersistenceManager createMockPersistenceManager() throws PersistenceException {
    PersistenceManager persistenceManager = mock(PersistenceManager.class);
    given(persistenceManager.persistURL(anyString())).willReturn("testPersistentId");
    given(persistenceManager.getPersistentURL(anyString())).willReturn("testPersistentUrl");
    return persistenceManager;
  }

  private EntityLookup createMockEntityLookup(User user) {
    EntityLookup entityLookup = mock(EntityLookup.class);
    given(entityLookup.getDataSetId()).willReturn(Optional.of("testOwnerId__testDataSetId"));
    given(entityLookup.getUser()).willReturn(Optional.of(user));
    given(entityLookup.getUri()).willReturn(Optional.of("testEntityUri"));
    return entityLookup;
  }

  private DataSetMetaData createMockDataSetMetaData() {
    DataSetMetaData dataSetMetaData = mock(DataSetMetaData.class);
    given(dataSetMetaData.getDataSetId()).willReturn("testDataSetId");
    given(dataSetMetaData.getOwnerId()).willReturn("testOwnerId");
    given(dataSetMetaData.getBaseUri()).willReturn("testOwnerId__testDataSetId");
    given(dataSetMetaData.getGraph()).willReturn("testOwnerId__testGraph");
    return dataSetMetaData;
  }

  private static class NonQueueQueueCreator<T> implements QueueCreator<T> {
    List<QueueSender> queueSenderList = new ArrayList<>();
    List<Consumer> receiverList = new ArrayList<>();

    @Override
    public void registerReceiver(Consumer consumer) {
      receiverList.add(consumer);
    }

    @Override
    public QueueSender createSender() {
      QueueSender queueSender = new NonQueueQueueSender(receiverList);
      queueSenderList.add(queueSender);
      return queueSender;
    }
  }

  private static class NonQueueQueueSender implements  QueueSender {
    private final List<Consumer> receiverList;

    public NonQueueQueueSender(List<Consumer> receiverList) {
      this.receiverList = receiverList;
    }

    @Override
    public void send(Object object) {
      receiverList.forEach(receiver -> receiver.accept(object));
    }
  }

  private static class LogListJsonDataStore implements JsonDataStore<LogList> {
    LogList logList = new LogList();

    @Override
    public void updateData(Function<LogList, LogList> mutator) throws IOException {
      mutator.apply(logList);
    }

    @Override
    public LogList getData() {
      return logList;
    }
  }
}
