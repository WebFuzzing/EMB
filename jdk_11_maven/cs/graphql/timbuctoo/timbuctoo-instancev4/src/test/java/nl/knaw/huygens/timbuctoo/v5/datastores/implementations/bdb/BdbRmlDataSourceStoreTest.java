package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;
import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.ThrowingErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.datasource.jexl.JexlRowFactory;
import nl.knaw.huygens.timbuctoo.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.bulkupload.RawUploadRdfSaver;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangeFetcher;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.OptimizedPatchListener;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.BasicDataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.rmldatasource.RmlDataSourceStore;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import nl.knaw.huygens.timbuctoo.v5.rml.RdfDataSource;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType.ASSERTED;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad.create;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.IN;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.LANGSTRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class BdbRmlDataSourceStoreTest {

  @Test
  public void itWorks() throws Exception {
    BdbNonPersistentEnvironmentCreator dbCreator = new BdbNonPersistentEnvironmentCreator();
    DataSetMetaData dataSetMetadata = new BasicDataSetMetaData(
        "userid",
        "datasetid",
        "http://timbuctoo.huygens.knaw.nl/v5/userid/datasetid",
        "http://timbuctoo.huygens.knaw.nl/v5/userid/datasetid",
        "http://example.org/prefix/", false, false
    );

    final RmlDataSourceStore rmlDataSourceStore = new BdbRmlDataSourceStore(
        dbCreator.getDatabase(
            "userid",
            "datasetid",
            "rmlSource",
            true,
            TupleBinding.getPrimitiveBinding(String.class),
            TupleBinding.getPrimitiveBinding(String.class),
            new StringStringIsCleanHandler()
        ),
        new ImportStatus(new LogList())
    );

    RdfSerializer rdfSerializer = new RmlDataSourceRdfSerializer(rmlDataSourceStore);
    RawUploadRdfSaver rawUploadRdfSaver = new RawUploadRdfSaver(
        dataSetMetadata,
        "fileName",
        APPLICATION_OCTET_STREAM_TYPE,
        rdfSerializer,
        "origFileName",
        Clock.systemUTC()
    );
    final String inputCol1 = rawUploadRdfSaver.addCollection("collection1");
    ImportPropertyDescriptions importPropertyDescriptions = new ImportPropertyDescriptions();
    importPropertyDescriptions.getOrCreate(1).setPropertyName("propName1");
    importPropertyDescriptions.getOrCreate(2).setPropertyName("propName2");
    rawUploadRdfSaver.addPropertyDescriptions(inputCol1, importPropertyDescriptions);
    rawUploadRdfSaver.addEntity(inputCol1, ImmutableMap.of("propName1", "value1", "propName2", "val2"));
    rawUploadRdfSaver.addEntity(inputCol1, ImmutableMap.of("propName1", "entVal1", "propName2", "entVal2"));
    final String inputCol2 = rawUploadRdfSaver.addCollection("collection2");
    ImportPropertyDescriptions importPropertyDescriptions1 = new ImportPropertyDescriptions();
    importPropertyDescriptions1.getOrCreate(1).setPropertyName("prop3");
    importPropertyDescriptions1.getOrCreate(2).setPropertyName("prop4");
    rawUploadRdfSaver.addPropertyDescriptions(inputCol2, importPropertyDescriptions1);
    rawUploadRdfSaver.addEntity(inputCol2, ImmutableMap.of("prop3", "value1", "prop4", "val2"));
    rawUploadRdfSaver.addEntity(inputCol2, ImmutableMap.of("prop3", "entVal1", "prop4", "entVal2"));
    rdfSerializer.close();

    RdfDataSource rdfDataSource = new RdfDataSource(
        rmlDataSourceStore,
        inputCol1,
        new JexlRowFactory(ImmutableMap.of(), new HashMapBasedJoinHandler())
    );
    RdfDataSource rdfDataSource2 = new RdfDataSource(
        rmlDataSourceStore,
        inputCol2,
        new JexlRowFactory(ImmutableMap.of(), new HashMapBasedJoinHandler())
    );

    final List<String> collection1;
    final List<String> collection2;
    try (Stream<Row> stream = rdfDataSource.getRows(new ThrowingErrorHandler())) {
      collection1 = stream
          .map(x -> x.getRawValue("propName1") + ":" + x.getRawValue("propName2"))
          .collect(toList());
    }
    try (Stream<Row> stream = rdfDataSource2.getRows(new ThrowingErrorHandler())) {
      collection2 = stream
          .map(x -> x.getRawValue("prop3") + ":" + x.getRawValue("prop4"))
          .collect(toList());
    }

    assertThat(collection1, contains("value1:val2", "entVal1:entVal2"));
    assertThat(collection2, contains("value1:val2", "entVal1:entVal2"));
    dbCreator.close();
  }

  private static class RmlDataSourceRdfSerializer implements RdfSerializer {
    private final OptimizedPatchListener processor;
    Map<String, Multimap<String, CursorQuad>> triples = new HashMap<>();

    public RmlDataSourceRdfSerializer(OptimizedPatchListener processor) {
      this.processor = processor;
    }

    @Override
    public MediaType getMediaType() {
      return null;
    }

    @Override
    public Charset getCharset() {
      return null;
    }

    @Override
    public void onPrefix(String prefix, String iri) throws LogStorageFailedException {
    }

    @Override
    public void onRelation(String subject, String predicate, String object, String graph)
        throws LogStorageFailedException {
      triples.computeIfAbsent(subject, s -> ArrayListMultimap.create())
             .put(predicate + "\n" + OUT, create(subject, predicate, OUT, ASSERTED, object, null, null, ""));
      triples.computeIfAbsent(object, s -> ArrayListMultimap.create())
             .put(predicate + "\n" + IN, create(object, predicate, IN, ASSERTED, subject, null, null, ""));
    }

    @Override
    public void onValue(String subject, String predicate, String value, String valueType, String graph)
        throws LogStorageFailedException {
      triples.computeIfAbsent(subject, s -> ArrayListMultimap.create())
             .put(predicate + "\n" + OUT, create(subject, predicate, OUT, ASSERTED, value, valueType, null, ""));
    }

    @Override
    public void onLanguageTaggedString(String subject, String predicate, String value, String language,
                                       String graph)
        throws LogStorageFailedException {
      triples.computeIfAbsent(subject, s -> ArrayListMultimap.create()).put(
          predicate + "\n" + OUT,
          create(subject, predicate, OUT, ASSERTED, value, LANGSTRING, language, "")
      );
    }

    @Override
    public void close() throws LogStorageFailedException {
      try {
        processor.start();
        for (Map.Entry<String, Multimap<String, CursorQuad>> subject : triples.entrySet()) {
          processor.onChangedSubject(subject.getKey(), new ChangeFetcher() {
            @Override
            public Stream<CursorQuad> getPredicates(String subject, boolean getRetracted, boolean getUnchanged,
                                                    boolean getAsserted) {
              if (triples.containsKey(subject) && (getUnchanged || getAsserted)) {
                return triples.get(subject).values().stream();
              } else {
                return Stream.empty();
              }
            }

            @Override
            public Stream<CursorQuad> getPredicates(String subject, String predicate, Direction direction,
                                                    boolean getRetracted,
                                                    boolean getUnchanged, boolean getAsserted) {
              if (triples.containsKey(subject) && getAsserted) {
                return triples.get(subject).get(predicate + "\n" + direction.name()).stream();
              } else {
                return Stream.empty();
              }
            }
          });
        }
        processor.finish();
      } catch (RdfProcessingFailedException e) {
        throw new LogStorageFailedException(e);
      }
    }
  }
}
