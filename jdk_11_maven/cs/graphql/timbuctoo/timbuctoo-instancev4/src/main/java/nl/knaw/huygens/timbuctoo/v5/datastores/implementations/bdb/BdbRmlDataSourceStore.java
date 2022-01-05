package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.util.StreamIterator;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangeFetcher;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.rmldatasource.RmlDataSourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDFS_LABEL;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HAS_PROPERTY;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_HAS_ROW;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

public class BdbRmlDataSourceStore implements RmlDataSourceStore {
  private static final Logger LOG = LoggerFactory.getLogger(BdbRmlDataSourceStore.class);
  protected final BdbWrapper<String, String> bdbWrapper;
  private final Map<String, Map<String, Property>> collectionProperties = new HashMap<>();
  private ImportStatus importStatus;

  public BdbRmlDataSourceStore(BdbWrapper<String, String> bdbWrapper, ImportStatus importStatus)
    throws DataStoreCreationException {
    this.bdbWrapper = bdbWrapper;
    this.importStatus = importStatus;
  }

  @Override
  public Stream<String> get(String collectionUri) {
    return bdbWrapper.databaseGetter()
      .key(collectionUri)
      .dontSkip()
      .forwards()
      .getValues(bdbWrapper.valueRetriever());
  }

  @Override
  public void close() {
    try {
      bdbWrapper.close();
    } catch (Exception e) {
      LOG.error("Exception closing BdbRmlDataSourceStore", e);
    }
  }

  @Override
  public boolean isClean() {
    return bdbWrapper.isClean();
  }

  @Override
  public void start() {
    importStatus.setStatus("Storing entities");
    bdbWrapper.beginTransaction();
  }

  @Override
  public void onChangedSubject(String subject, ChangeFetcher changeFetcher) throws RdfProcessingFailedException {
    try {
      boolean[] wasCollection = new boolean[]{false};
      StreamIterator.iterateAndCloseOrThrow(
        changeFetcher.getPredicates(subject, TIM_HAS_ROW, Direction.OUT, true, false, true),
        quad -> {
          wasCollection[0] = true;
          if (quad.getChangeType() == ChangeType.ASSERTED) {
            bdbWrapper.put(subject, quad.getObject());
          }
        }
      );
      if (!wasCollection[0]) {
        try (Stream<CursorQuad> quads =
               changeFetcher.getPredicates(subject, TIM_HAS_ROW, Direction.IN, true, true, true)) {
          Optional<CursorQuad> isRawRow = quads.findFirst();
          if (isRawRow.isPresent()) {
            final String collectionUri = isRawRow.get().getObject();
            Map<String, Property> predicatesToStore = collectionProperties.computeIfAbsent(
              collectionUri,
              collection -> getPropertyNames(changeFetcher, collection)
            );
            switch (isRawRow.get().getChangeType()) {
              case ASSERTED:
                //add all unchanged and new predicates
                StreamIterator.iterateAndCloseOrThrow(
                  changeFetcher.getPredicates(subject, false, true, true),
                  pred -> {
                    final Property property = predicatesToStore.get(pred.getPredicate());
                    if (property != null) {
                      bdbWrapper.put(subject, property.newName + "\n" + pred.getObject());
                    }
                  }
                );
                break;
              case RETRACTED:
                //remove all unchanged and removed predicates
                StreamIterator.iterateAndCloseOrThrow(
                  changeFetcher.getPredicates(subject, true, true, false),
                  pred -> {
                    final Property property = predicatesToStore.get(pred.getPredicate());
                    if (property != null) {
                      bdbWrapper.delete(subject, property.oldName + "\n" + pred.getObject());
                    }
                  }
                );
                break;
              case UNCHANGED:
                //add all added predicates, remove all removed predicates
                StreamIterator.iterateAndCloseOrThrow(
                  changeFetcher.getPredicates(subject, true, false, true),
                  pred -> {
                    final Property property = predicatesToStore.get(pred.getPredicate());
                    if (property != null) {
                      if (pred.getChangeType() == ChangeType.RETRACTED) {
                        bdbWrapper.delete(subject, property.oldName + "\n" + pred.getObject());
                      } else {
                        bdbWrapper.put(subject, property.oldName + "\n" + pred.getObject());
                      }
                    }
                  }
                );
                break;
              default:
                throw new RuntimeException("Should not happen");
            }
          }
        }
      }
    } catch (DatabaseWriteException e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  public Map<String, Property> getPropertyNames(ChangeFetcher changeFetcher, String collection) {
    Map<String, Property> result = new HashMap<>();
    StreamIterator.iterateAndClose(
      changeFetcher.getPredicates(collection, TIM_HAS_PROPERTY, Direction.OUT, true, true, true),
      propertyQuad -> {
        Property propDesc = new Property();
        StreamIterator.iterateAndClose(
          changeFetcher.getPredicates(propertyQuad.getObject(), RDFS_LABEL, Direction.OUT, true, true, true),
          label -> {
            if (label.getChangeType() == ChangeType.RETRACTED) {
              propDesc.oldName = escapeJava(label.getObject());
            } else {
              propDesc.newName = escapeJava(label.getObject());
            }
          }
        );
        result.put(propertyQuad.getObject(), propDesc);
      }
    );
    return result;
  }

  @Override
  public void notifyUpdate() {
  }

  @Override
  public void finish() {
    bdbWrapper.commit();
  }

  public void empty() {
    bdbWrapper.empty();
  }

  private class Property {
    String oldName;
    String newName;
  }
}
