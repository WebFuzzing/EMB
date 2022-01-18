package nl.knaw.huygens.timbuctoo.v5.bulkupload;

import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescription;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;
import nl.knaw.huygens.timbuctoo.bulkupload.savers.Saver;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.INTEGER;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.PROV_ATTIME;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.PROV_DERIVED_FROM;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDFS_LABEL;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIMBUCTOO_NEXT;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HASCOLLECTION;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HAS_PROPERTY;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HAS_ROW;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_MIMETYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_PROP_DESC;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_PROP_ID;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_TABULAR_COLLECTION;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_TABULAR_FILE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.XSD_DATETIMESTAMP;

public class RawUploadRdfSaver implements Saver<String> {

  private static final Logger LOG = LoggerFactory.getLogger(RawUploadRdfSaver.class);
  private final String graph;
  private final RdfSerializer saver;
  private String prevCollection;
  private int curEntity;
  private int curCollection;

  private final String fileUri;

  public RawUploadRdfSaver(DataSetMetaData dataSet, String fileName, MediaType mimeType,
                           RdfSerializer saver, String origFilename, Clock clock)
    throws LogStorageFailedException {
    this.saver = saver;
    this.curEntity = 0;
    this.curCollection = 0;

    this.graph = dataSet.getGraph();
    String prefix = dataSet.getUriPrefix();
    fileUri = prefix + "rawData/" + encode(fileName) + "/";
    this.saver.onRelation(fileUri, RDF_TYPE, TIM_TABULAR_FILE, this.graph);
    this.saver.onRelation(this.graph, PROV_DERIVED_FROM, fileUri, this.graph);
    this.saver.onValue(fileUri, TIM_MIMETYPE, mimeType.toString(), STRING, this.graph);
    this.saver.onValue(fileUri, RDFS_LABEL, origFilename, STRING, this.graph);
    this.saver.onValue(fileUri, PROV_ATTIME, clock.instant().toString(), XSD_DATETIMESTAMP, this.graph);
    this.prevCollection = fileUri;
  }

  @Override
  public String addEntity(String collection, Map<String, String> currentProperties) {
    String subject = rawEntity(++curEntity);

    try {
      saver.onRelation(subject, RDF_TYPE, collection, graph);
      saver.onRelation(collection, TIM_HAS_ROW, subject, graph);
    } catch (LogStorageFailedException e) {
      LOG.error("Could not save entity", e);
    }

    for (Map.Entry<String, String> property : currentProperties.entrySet()) {
      try {
        String propName = propertyDescription(property.getKey());
        saver.onValue(subject, propName, property.getValue(), STRING, graph);
      } catch (LogStorageFailedException e) {
        LOG.error("Could not store value", e);
      }
    }

    String timIdPropname = propertyDescription("tim_id");
    try {
      saver.onValue(subject, timIdPropname, UUID.randomUUID().toString(), STRING, graph);
    } catch (LogStorageFailedException e) {
      LOG.error("Could not add tim_id property.");
    }


    return subject;
  }

  @Override
  public String addCollection(String collectionName) {
    String subject = rawCollection(++curCollection);

    try {
      saver.onRelation(subject, RDF_TYPE, subject + "type", graph);
      saver.onRelation(subject, RDF_TYPE, TIM_TABULAR_COLLECTION, graph);
      saver.onValue(subject, RDFS_LABEL, collectionName, STRING, graph);
      saver.onRelation(fileUri, TIM_HASCOLLECTION, subject, graph); //for getting all collections in a file
      saver.onRelation(prevCollection, TIMBUCTOO_NEXT, subject, graph); //for getting the ordered collections
      prevCollection = subject;
    } catch (LogStorageFailedException e) {
      LOG.error("Could not store value", e);
    }

    return subject;
  }

  @Override
  public void addPropertyDescriptions(String collection, ImportPropertyDescriptions importPropertyDescriptions) {
    SortedMap<Integer, ImportPropertyDescription> sortedDescriptions = new TreeMap<>();
    importPropertyDescriptions.forEach(prop -> {
      sortedDescriptions.put(prop.getOrder(), prop);
    });
    String prevPropUri = addPropertyDescription(collection, "tim_id", -1, null);
    for (ImportPropertyDescription prop : sortedDescriptions.values()) {
      String propertyName = prop.getPropertyName();
      Integer id = prop.getId();
      prevPropUri = addPropertyDescription(collection, propertyName, id, prevPropUri);
    }
  }

  public String addPropertyDescription(String collection, String propertyName, Integer id, String prevPropUri) {
    String propertyUri = propertyDescription(propertyName);
    try {
      saver.onRelation(propertyUri, RDF_TYPE, TIM_PROP_DESC, graph);
      saver.onRelation(collection, TIM_HAS_PROPERTY, propertyUri, graph);
      saver.onValue(propertyUri, TIM_PROP_ID, "" + id, INTEGER, graph);
      saver.onValue(propertyUri, RDFS_LABEL, propertyName, STRING, graph);
      if (prevPropUri != null) {
        saver.onRelation(prevPropUri, TIMBUCTOO_NEXT, propertyUri, graph);
      }
    } catch (LogStorageFailedException e) {
      LOG.error("Could not add property description", e);
    }

    return propertyUri;
  }

  public String rawEntity(int entityId) {
    return fileUri + "entities/" + entityId;
  }

  public String rawCollection(int collectionId) {
    return fileUri + "collections/" + collectionId;
  }

  public String propertyDescription(String propertyName) {
    return fileUri + "props/" + encode(propertyName);
  }

  private static String encode(String input) {
    return URLEncoder.encode(input, StandardCharsets.UTF_8);
  }
}
