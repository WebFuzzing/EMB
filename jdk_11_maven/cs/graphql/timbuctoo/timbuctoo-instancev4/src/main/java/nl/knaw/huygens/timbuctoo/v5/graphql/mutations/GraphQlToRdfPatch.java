package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.dataset.PatchRdfCreator;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ChangeLog;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.dataSetObjectUri;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.timPredicate;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.timType;

public class GraphQlToRdfPatch implements PatchRdfCreator {
  private static final String PROV_SPECIALIZATION_OF = "http://www.w3.org/ns/prov#specializationOf";
  private static final String PROV_GENERATED = "http://www.w3.org/ns/prov#generated";
  private static final String PROV_ACTIVITY = "http://www.w3.org/ns/prov#Activity";
  private static final String PROV_ASSOCIATED_WITH = "http://www.w3.org/ns/prov#associatedWith";
  private static final String PROV_AGENT = "http://www.w3.org/ns/prov#Agent";
  private static final String PROV_QUALIFIED_ASSOCIATION = "http://www.w3.org/ns/prov#qualifiedAssociation";
  private static final String PROV_ASSOCIATION = "http://www.w3.org/ns/prov#Association";
  private static final String PROV_AGENT_PRED = "http://www.w3.org/ns/prov#agent";
  private static final String PROV_HAD_PLAN = "http://www.w3.org/ns/prov#hadPlan";
  private static final String PROV_PLAN = "http://www.w3.org/ns/prov#Plan";

  @JsonProperty
  private final String subjectUri; // move subject uri to ChangeLog
  @JsonProperty
  private final String userUri;
  @JsonProperty
  private final ChangeLog changeLog;

  @JsonCreator
  public GraphQlToRdfPatch(
    @JsonProperty("subjectUri") String subjectUri,
    @JsonProperty("userUri") String userUri,
    @JsonProperty("changeLog") ChangeLog changeLog
  ) {
    this.subjectUri = subjectUri;
    this.userUri = userUri;
    this.changeLog = changeLog;
  }

  @Override
  public void sendQuads(RdfPatchSerializer saver, Consumer<String> importStatusConsumer, DataSet dataSet)
    throws LogStorageFailedException {

    //for more information see /  documentation/design/tim-default-provenance.adoc

    addData(saver, dataSet);

    String newRevision = addNewRevision(saver, dataSet);
    String association = addProvenance(saver, dataSet, newRevision);

    addPlan(saver, dataSet, newRevision, association);
  }

  private String addNewRevision(RdfPatchSerializer saver, DataSet dataSet) throws LogStorageFailedException {
    int lastVersion = -1;
    try (Stream<CursorQuad> latestRevisionQuads = dataSet.getQuadStore().getQuads(
      subjectUri,
      timPredicate("latestRevision"),
      Direction.OUT,
      ""
    )) {
      CursorQuad latestRevision = latestRevisionQuads.findFirst().orElse(null);
      if (latestRevision != null) {
        try (Stream<CursorQuad> versionQuads = dataSet.getQuadStore().getQuads(
          latestRevision.getObject(),
          timPredicate("version"),
          Direction.OUT,
          ""
        )) {
          CursorQuad version = versionQuads.findFirst().orElse(null);
          if (version != null) {
            lastVersion = new Integer(version.getObject());
          }
        }
      }
    }

    String newRevision = subjectUri + "/" + (lastVersion + 1);
    saver.addDelQuad(true, subjectUri, timPredicate("latestRevision"), newRevision, null, null, null);
    saver.addDelQuad(true, newRevision, PROV_SPECIALIZATION_OF, subjectUri, null, null, null);
    saver.addDelQuad(true, newRevision, timPredicate("version"), String.valueOf(lastVersion + 1), RdfConstants.INTEGER,
      null, null);
    // remove previous latest revision predicate
    String prevRevision = subjectUri + "/" + lastVersion;
    saver.addDelQuad(false, subjectUri, timPredicate("latestRevision"), prevRevision, null, null, null);

    return newRevision;
  }

  private String addProvenance(RdfPatchSerializer saver, DataSet dataSet, String newRevision)
    throws LogStorageFailedException {
    String activity = dataSetObjectUri(dataSet, "activity");
    saver.addDelQuad(true, activity, PROV_GENERATED, newRevision, null, null, null);
    saver.addDelQuad(true, activity, RDF_TYPE, PROV_ACTIVITY, null, null, null);

    saver.addDelQuad(true, activity, PROV_ASSOCIATED_WITH, userUri, null, null, null);
    saver.addDelQuad(true, userUri, RDF_TYPE, PROV_AGENT, null, null, null);

    String association = dataSetObjectUri(dataSet, "association");
    saver.addDelQuad(true, activity, PROV_QUALIFIED_ASSOCIATION, association, null, null, null);
    saver.addDelQuad(true, association, RDF_TYPE, PROV_ASSOCIATION, null, null, null);
    saver.addDelQuad(true, association, PROV_AGENT_PRED, userUri, null, null, null);

    return association;
  }

  private void addPlan(RdfPatchSerializer saver, DataSet dataSet, String newRevision, String association)
    throws LogStorageFailedException {
    String planUri = dataSetObjectUri(dataSet, "plan");
    saver.addDelQuad(true, association, PROV_HAD_PLAN, planUri, null, null, null);
    saver.addDelQuad(true, planUri, RDF_TYPE, PROV_PLAN, null, null, null);

    // fill plan

    // add additions
    boolean additionsPresent;
    try (Stream<Change> additions = changeLog.getAdditions(dataSet)) {
      additionsPresent = additions.findAny().isPresent();
    }
    if (additionsPresent) {
      try (Stream<Change> additions = changeLog.getAdditions(dataSet)) {
        String actionPluralUri = dataSetObjectUri(dataSet, "additions");
        saver.addDelQuad(true, planUri, timPredicate("additions"), actionPluralUri, null, null, null);
        saver.addDelQuad(true, actionPluralUri, RDF_TYPE, timType("Additions"), null, null, null);
        for (Iterator<Change> changes = additions.iterator(); changes.hasNext(); ) {
          Change change = changes.next();
          String changeUri = dataSetObjectUri(dataSet, "addition");
          saver.addDelQuad(true, actionPluralUri, timPredicate("hasAddition"), changeUri, null, null, null);
          saver.addDelQuad(true, changeUri, RDF_TYPE, timType("Addition"), null, null, null);
          String predicate = change.getPredicate();
          saver.addDelQuad(true, changeUri, timPredicate("hasKey"), predicate, null, null, null);
          saver.addDelQuad(true, predicate, RDF_TYPE, timType("ChangeKey"), null, null, null);
          String prefValueUri = null;
          for (Value value : change.getValues()) {
            String uri = dataSetObjectUri(dataSet, "value");
            createValue(saver, changeUri, prefValueUri, value, uri);
            prefValueUri = uri;
          }
        }
      }
    }
    // add deletions
    boolean deletionsPresent;
    try (Stream<Change> deletions = changeLog.getDeletions(dataSet)) {
      deletionsPresent = deletions.findAny().isPresent();
    }
    if (deletionsPresent) {
      try (Stream<Change> deletions = changeLog.getDeletions(dataSet)) {
        String actionPluralUri = dataSetObjectUri(dataSet, "deletions");
        saver.addDelQuad(true, planUri, timPredicate("deletions"), actionPluralUri, null, null, null);
        saver.addDelQuad(true, actionPluralUri, RDF_TYPE, timType("Deletions"), null, null, null);
        for (Iterator<Change> changes = deletions.iterator(); changes.hasNext(); ) {
          Change change = changes.next();
          String changeUri = dataSetObjectUri(dataSet, "deletion");
          saver.addDelQuad(true, actionPluralUri, timPredicate("hasDeletion"), changeUri, null, null, null);
          saver.addDelQuad(true, changeUri, RDF_TYPE, timType("Deletion"), null, null, null);
          String predicate = change.getPredicate();
          saver.addDelQuad(true, changeUri, timPredicate("hasKey"), predicate, null, null, null);
          saver.addDelQuad(true, predicate, RDF_TYPE, timType("ChangeKey"), null, null, null);
          String prefValueUri = null;
          try (Stream<Value> oldValues = change.getOldValues()) {
            for (Iterator<Value> values = oldValues.iterator(); values.hasNext(); ) {
              Value value = values.next();
              String uri = dataSetObjectUri(dataSet, "value");
              createValue(saver, changeUri, prefValueUri, value, uri);
              prefValueUri = uri;
            }
          }
        }
      }
    }
    // add replacements
    boolean replacementsPresent;
    try (Stream<Change> replacements = changeLog.getReplacements(dataSet)) {
      replacementsPresent = replacements.findAny().isPresent();
    }
    if (replacementsPresent) {
      try (Stream<Change> replacements = changeLog.getReplacements(dataSet)) {
        String actionPluralUri = dataSetObjectUri(dataSet, "replacements");
        saver.addDelQuad(true, planUri, timPredicate("replacements"), actionPluralUri, null, null, null);
        saver.addDelQuad(true, actionPluralUri, RDF_TYPE, timType("Replacements"), null, null, null);
        for (Iterator<Change> changes = replacements.iterator(); changes.hasNext(); ) {
          Change change = changes.next();
          String changeUri = dataSetObjectUri(dataSet, "replacement");
          saver.addDelQuad(true, actionPluralUri, timPredicate("hasReplacement"), changeUri, null, null, null);
          saver.addDelQuad(true, changeUri, RDF_TYPE, timType("Replacement"), null, null, null);
          String predicate = change.getPredicate();
          saver.addDelQuad(true, changeUri, timPredicate("hasKey"), predicate, null, null, null);
          saver.addDelQuad(true, predicate, RDF_TYPE, timType("ChangeKey"), null, null, null);
          String prefValueUri = null;
          for (Iterator<Value> values = change.getValues().iterator(); values.hasNext(); ) {
            Value value = values.next();
            String uri = dataSetObjectUri(dataSet, "value");
            createValue(saver, changeUri, prefValueUri, value, uri);
            prefValueUri = uri;
          }
          try (Stream<Value> oldValues = change.getOldValues()) {
            for (Iterator<Value> values = oldValues.iterator(); values.hasNext(); ) {
              Value value = values.next();
              String uri = dataSetObjectUri(dataSet, "oldValue");
              createOldValue(saver, changeUri, prefValueUri, value, uri);
              prefValueUri = uri;
            }
          }
        }
      }
    }
    // add custom provenance
    if (changeLog.getProvenance(dataSet, subjectUri).findAny().isPresent()) {
      String customProvUri = dataSetObjectUri(dataSet, "customProv");
      saver.addDelQuad(true, planUri, timPredicate("hasCustomProv"), customProvUri, null, null, null);
      saver.addDelQuad(true, customProvUri, RDF_TYPE, timType("CustomProv"), null, null, null);

      Stream<Change> customProvChanges = changeLog.getProvenance(dataSet, newRevision, customProvUri);
      for (Iterator<Change> changes = customProvChanges.iterator(); changes.hasNext(); ) {
        Change change = changes.next();
        for (Value value : change.getValues()) {
          saver.addDelQuad(true, change.getSubject(), change.getPredicate(), value.getRawValue(), value.getType(), null,
            null);
        }
      }
    }
  }

  private void createOldValue(RdfPatchSerializer saver, String changeUri, String prefValueUri, Value value,
                              String uri) throws LogStorageFailedException {
    saver.addDelQuad(true, uri, RDF_TYPE, timType("OldValue"), null, null, null);
    saver.addDelQuad(true, changeUri, timPredicate("hadValue"), uri, null, null, null);
    if (value.getType() != null) {
      saver.addDelQuad(true, uri, timPredicate("type"), value.getType(), STRING, null, null);
    }
    saver.addDelQuad(true, uri, timPredicate("rawValue"), value.getRawValue(), STRING, null, null);
    if (prefValueUri != null) {
      saver.addDelQuad(true, prefValueUri, timPredicate("nextOldValue"), uri, null, null, null);
    }
  }

  private void createValue(RdfPatchSerializer saver, String changeUri, String prefValueUri, Value value,
                           String uri) throws LogStorageFailedException {
    saver.addDelQuad(true, uri, RDF_TYPE, timType("Value"), null, null, null);
    saver.addDelQuad(true, changeUri, timPredicate("hasValue"), uri, null, null, null);
    if (value.getType() != null) {
      saver.addDelQuad(true, uri, timPredicate("type"), value.getType(), STRING, null, null);
    }
    saver.addDelQuad(true, uri, timPredicate("rawValue"), value.getRawValue(), STRING, null, null);
    if (prefValueUri != null) {
      saver.addDelQuad(true, prefValueUri, timPredicate("nextValue"), uri, null, null, null);
    }
  }

  private void addData(RdfPatchSerializer saver, DataSet dataSet) throws LogStorageFailedException {
    try (Stream<Change> deletions = changeLog.getDeletions(dataSet)) {
      for (Iterator<Change> changes = deletions.iterator(); changes.hasNext(); ) {
        Change change = changes.next();
        String predicate = change.getPredicate();
        try (Stream<Value> oldValues = change.getOldValues()) {
          for (Iterator<Value> iterator = oldValues.iterator(); iterator.hasNext(); ) {
            Value value = iterator.next();
            saver.addDelQuad(false, subjectUri, predicate, value.getRawValue(), value.getType(), null, null);
          }
        }
      }
    }

    try (Stream<Change> replacements = changeLog.getReplacements(dataSet)) {
      for (Iterator<Change> changes = replacements.iterator(); changes.hasNext(); ) {
        Change change = changes.next();
        String predicate = change.getPredicate();
        try (Stream<Value> oldValues = change.getOldValues()) {
          for (Iterator<Value> iterator = oldValues.iterator(); iterator.hasNext(); ) {
            Value value = iterator.next();
            saver.addDelQuad(false, subjectUri, predicate, value.getRawValue(), value.getType(), null, null);
          }
        }
        for (Value value : change.getValues()) {
          saver.addDelQuad(true, subjectUri, predicate, value.getRawValue(), value.getType(), null, null);
        }
      }
    }

    try (Stream<Change> additions = changeLog.getAdditions(dataSet)) {
      for (Iterator<Change> changes = additions.iterator(); changes.hasNext(); ) {
        Change change = changes.next();
        for (Value value : change.getValues()) {
          saver.addDelQuad(true, subjectUri, change.getPredicate(), value.getRawValue(), value.getType(), null, null);
        }
      }
    }
  }
}
