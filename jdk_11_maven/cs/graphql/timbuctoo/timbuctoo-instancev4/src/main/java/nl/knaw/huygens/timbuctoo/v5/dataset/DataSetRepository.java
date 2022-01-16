package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbEnvironmentCreator;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.BasicDataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportInfo;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetPublishException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.IllegalDataSetNameException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.NotEnoughPermissionsException;
import nl.knaw.huygens.timbuctoo.v5.datastorage.DataStorage;
import nl.knaw.huygens.timbuctoo.v5.datastorage.exceptions.DataStorageSaveException;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import nl.knaw.huygens.timbuctoo.v5.util.TimbuctooRdfIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet.dataSet;
import static nl.knaw.huygens.timbuctoo.v5.security.dto.Permission.PUBLISH_DATASET;
import static nl.knaw.huygens.timbuctoo.v5.security.dto.Permission.READ;

/**
 * - stores all configuration parameters so it can inject them in the dataset constructor
 * - makes CreateDataSet a singleton
 * - keeps track of all created dataSets across restarts (stores them in a file)
 */
public class DataSetRepository {
  private static final Logger LOG = LoggerFactory.getLogger(DataSetRepository.class);

  private final ExecutorService executorService;
  private final PermissionFetcher permissionFetcher;
  private final BdbEnvironmentCreator dataStoreFactory;
  private final Map<String, Map<String, DataSet>> dataSetMap;
  private final TimbuctooRdfIdHelper rdfIdHelper;
  private final String rdfBaseUri;
  private final boolean publicByDefault;
  private final ReadOnlyChecker readOnlyChecker;
  private final List<Runnable> dataSetsUpdatedListeners;
  private Consumer<String> onUpdated;
  private final DataStorage dataStorage;

  public DataSetRepository(ExecutorService executorService, PermissionFetcher permissionFetcher,
                           BdbEnvironmentCreator dataStoreFactory,
                           TimbuctooRdfIdHelper rdfIdHelper, Consumer<String> onUpdated,
                           boolean publicByDefault, DataStorage dataStorage) {
    this.executorService = executorService;
    this.permissionFetcher = permissionFetcher;
    this.dataStoreFactory = dataStoreFactory;

    this.rdfIdHelper = rdfIdHelper;
    this.rdfBaseUri = rdfIdHelper.instanceBaseUri();
    this.publicByDefault = publicByDefault;
    dataSetMap = new HashMap<>();
    this.onUpdated = onUpdated;
    this.dataStorage = dataStorage;
    readOnlyChecker = new ReadOnlyChecker() {
      @Override
      public boolean isReadonlyPredicate(String predicateIri) {
        return predicateIri.equals(RdfConstants.RDF_TYPE) ||
            predicateIri.equals(RdfConstants.timPredicate("latestRevision")) ||
            predicateIri.equals(RdfConstants.timPredicate("version")) ||
            predicateIri.equals(RdfConstants.timPredicate("deletions")) ||
            predicateIri.equals(RdfConstants.timPredicate("hasDeletion")) ||
            predicateIri.equals(RdfConstants.timPredicate("additions")) ||
            predicateIri.equals(RdfConstants.timPredicate("hasAddition")) ||
            predicateIri.equals(RdfConstants.timPredicate("replacements")) ||
            predicateIri.equals(RdfConstants.timPredicate("hasReplacement")) ||
            predicateIri.equals(RdfConstants.timPredicate("hasKey")) ||
            predicateIri.equals(RdfConstants.timPredicate("hasValue")) ||
            predicateIri.equals(RdfConstants.timPredicate("type")) ||
            predicateIri.equals(RdfConstants.timPredicate("rawValue")) ||
            predicateIri.equals(RdfConstants.timPredicate("nextValue")) ||
            RdfConstants.isProvenance(predicateIri);
      }

      @Override
      public boolean isReadonlyType(String typeUri) {
        return RdfConstants.isProvenance(typeUri) ||
            RdfConstants.UNKNOWN.equals(typeUri) ||
            RdfConstants.timType("Additions").equals(typeUri) ||
            RdfConstants.timType("Addition").equals(typeUri) ||
            RdfConstants.timType("Deletions").equals(typeUri) ||
            RdfConstants.timType("Deletion").equals(typeUri) ||
            RdfConstants.timType("Replacements").equals(typeUri) ||
            RdfConstants.timType("Replacement").equals(typeUri) ||
            RdfConstants.timType("ChangeKey").equals(typeUri) ||
            RdfConstants.timType("Value").equals(typeUri);
      }
    };
    dataSetsUpdatedListeners = Lists.newArrayList();
  }

  private void loadDataSetsFromJson() throws IOException {
    Map<String, Set<DataSetMetaData>> ownerMetadataMap = dataStorage.loadDataSetMetaData();
    synchronized (dataSetMap) {
      for (Map.Entry<String, Set<DataSetMetaData>> entry : ownerMetadataMap.entrySet()) {
        String ownerId = entry.getKey();
        Set<DataSetMetaData> ownerMetaDatas = entry.getValue();
        HashMap<String, DataSet> ownersSets = new HashMap<>();
        dataSetMap.put(ownerId, ownersSets);
        for (DataSetMetaData dataSetMetaData : ownerMetaDatas) {
          String dataSetName = dataSetMetaData.getDataSetId();
          try {
            DataSet value = dataSet(
              dataSetMetaData,
              executorService,
              rdfBaseUri,
              dataStoreFactory,
              () -> onUpdated.accept(dataSetMetaData.getCombinedId()),
              dataStorage.getDataSetStorage(ownerId, dataSetName),
              readOnlyChecker
            );
            ownersSets.put(
              dataSetName,
              value
            );
            dataSetsUpdatedListeners.forEach(value::subscribeToDataChanges);
          } catch (DataStoreCreationException e) {
            throw new IOException(e);
          }
        }
      }
    }
  }

  /**
   * Method to reload a data set.
   * This method should only be used by the {@link nl.knaw.huygens.timbuctoo.v5.dropwizard.tasks.ReloadDataSet}.
   *
   * @param dataSetId the combined id of data set to reload
   */
  public void reloadDataSet(String dataSetId) throws IOException, DataStoreCreationException {
    final Tuple<String, String> userDataSet = DataSetMetaData.splitCombinedId(dataSetId);
    final String userId = userDataSet.getLeft();
    if (dataSetMap.containsKey(userId)) {
      final Map<String, DataSet> userSets = dataSetMap.get(userId);
      final String dataSetName = userDataSet.getRight();
      if (userSets.containsKey(dataSetName)) {
        final DataSet dataSet = userSets.remove(dataSetName);
        final DataSetMetaData metadata = dataSet.getMetadata();
        dataSet.stop();
        final DataSet reloadedDataSet = dataSet(
          metadata,
          executorService,
          rdfBaseUri,
          dataStoreFactory,
          () -> onUpdated.accept(metadata.getCombinedId()),
          dataStorage.getDataSetStorage(userId, dataSetName),
          readOnlyChecker
        );
        userSets.put(dataSetName, reloadedDataSet);
      }
    }
  }

  /**
   * Gets the dataSet designated by <code>ownerId</code> and <code>dataSetId</code> but only if the given
   * <code>user</code> has read-access to the dataSet.
   *
   * @param user      the user that wants read-access, may be <code>null</code>
   * @param ownerId   ownerId
   * @param dataSetId dataSetId
   * @return the dataSet designated by <code>ownerId</code> and <code>dataSetId</code>
   */
  public Optional<DataSet> getDataSet(User user, String ownerId, String dataSetId) {
    synchronized (dataSetMap) {
      if (dataSetMap.containsKey(ownerId) && dataSetMap.get(ownerId).containsKey(dataSetId)) {
        try {
          if (permissionFetcher.hasPermission(user, dataSetMap.get(ownerId).get(dataSetId).getMetadata(), READ)) {
            return Optional.ofNullable(dataSetMap.get(ownerId).get(dataSetId));
          }
        } catch (PermissionFetchingException e) {
          return Optional.empty();
        }
      }
      return Optional.empty();
    }
  }

  public Optional<DataSet> unsafeGetDataSetWithoutCheckingPermissions(String ownerId, String dataSetId) {
    synchronized (dataSetMap) {
      if (dataSetMap.containsKey(ownerId) && dataSetMap.get(ownerId).containsKey(dataSetId)) {
        return Optional.ofNullable(dataSetMap.get(ownerId).get(dataSetId));
      } else {
        return Optional.empty();
      }
    }
  }

  public boolean userMatchesPrefix(User user, String prefix) {
    return user != null && user.getPersistentId() != null && ("u" + user.getPersistentId()).equals(prefix);
  }

  public DataSet createDataSet(User user, String dataSetId) throws DataStoreCreationException,
      IllegalDataSetNameException, DataSetCreationException {
    return createDataSet(user, dataSetId, null);
  }


  public DataSet createDataSet(User user, String dataSetId,
                               List<ImportInfo> importInfos) throws DataStoreCreationException,
      IllegalDataSetNameException, DataSetCreationException {
    //The ownerId might not be valid (i.e. a safe string). We make it safe here:
    //dataSetId is under the control of the user so we simply throw if it's not valid
    String ownerPrefix = "u" + user.getPersistentId();
    if (dataStorage.dataSetExists(ownerPrefix, dataSetId)) {
      throw new DataSetCreationException("DataSet already exists on disk.");
    }
    final String baseUri = rdfIdHelper.dataSetBaseUri(ownerPrefix, dataSetId);
    String uriPrefix;
    if (!baseUri.endsWith("/") && !baseUri.endsWith("#") && !baseUri.endsWith("?")) {
      //it might have some parts

      //?foo
      //?foo=bar
      //?boo&foo
      //?boo&foo=bar
      //#foo
      //#foo=bar
      //#boo&foo
      //#boo&foo=bar
      if (baseUri.contains("#") || baseUri.contains("?")) {
        if (baseUri.endsWith("&")) {
          uriPrefix = baseUri;
        } else {
          uriPrefix = baseUri + "&";
        }
      } else {
        uriPrefix = baseUri + "/";
      }
    } else {
      uriPrefix = baseUri;
    }

    String graph = baseUri.endsWith("/") ? baseUri.substring(0, baseUri.length() - 1) : baseUri;

    final DataSetMetaData dataSet = new BasicDataSetMetaData(
      ownerPrefix,
      dataSetId,
      baseUri,
      graph,
      uriPrefix,
      false,
      publicByDefault,
      importInfos
    );

    try {
      dataStorage.getDataSetStorage(ownerPrefix, dataSetId).saveMetaData(dataSet);
    } catch (DataStorageSaveException e) {
      throw new DataStoreCreationException(e);
    }

    synchronized (dataSetMap) {
      Map<String, DataSet> userDataSets = dataSetMap.computeIfAbsent(ownerPrefix, key -> new HashMap<>());

      if (!userDataSets.containsKey(dataSetId)) {
        try {
          permissionFetcher.initializeOwnerAuthorization(user, dataSet.getOwnerId(), dataSet.getDataSetId());
          DataSet createdDataset = dataSet(
            dataSet,
            executorService,
            rdfBaseUri,
            dataStoreFactory,
            () -> onUpdated.accept(dataSet.getCombinedId()),
            dataStorage.getDataSetStorage(ownerPrefix, dataSetId), readOnlyChecker);
          userDataSets.put(
            dataSetId,
            createdDataset
          );
          dataSetsUpdatedListeners.forEach(createdDataset::subscribeToDataChanges);
        } catch (PermissionFetchingException | AuthorizationCreationException | IOException e) {
          LOG.error("Could not create data set", e);
          throw new DataStoreCreationException(e);
        }
      }
      return userDataSets.get(dataSetId);
    }
  }

  boolean dataSetExists(String ownerId, String dataSet) {
    return unsafeGetDataSetWithoutCheckingPermissions(ownerId, dataSet).isPresent();
  }

  public void publishDataSet(User user, String ownerId, String dataSetName)
      throws DataSetPublishException {
    Optional<DataSet> dataSet = getDataSet(user, ownerId, dataSetName);
    try {
      if (dataSet.isPresent() && permissionFetcher.hasPermission(user, dataSet.get().getMetadata(), PUBLISH_DATASET)) {
        DataSetMetaData dataSetMetaData = dataSet.get().getMetadata();

        dataSetMetaData.publish();

        try {
          dataStorage.getDataSetStorage(ownerId, dataSetName).saveMetaData(dataSetMetaData);
        } catch (DataStorageSaveException e) {
          throw new DataSetPublishException(e);
        }
      }
    } catch (PermissionFetchingException e) {
      throw new DataSetPublishException(e);
    }
  }

  public Collection<DataSet> getDataSets() {
    return dataSetMap.values().stream().flatMap(x -> x.values().stream())
                     .collect(Collectors.toList());
  }

  public Collection<DataSet> getPromotedDataSets() {
    return dataSetMap.values().stream().flatMap(x -> x.values().stream())
                     .filter(x -> x.getMetadata().isPromoted())
                     .collect(Collectors.toList());
  }

  public Collection<DataSet> getDataSetsWithWriteAccess(User user) {
    List<DataSet> dataSetsWithWriteAccess = new ArrayList<>();

    for (Map<String, DataSet> userDataSets : dataSetMap.values()) {
      for (DataSet dataSet : userDataSets.values()) {
        try {
          if (permissionFetcher.hasPermission(user, dataSet.getMetadata(), Permission.WRITE)) {
            dataSetsWithWriteAccess.add(dataSet);
          }
        } catch (PermissionFetchingException e) {
          LOG.error("Could not fetch write permission", e);
        }
      }
    }
    return dataSetsWithWriteAccess;
  }

  /**
   * Gets all published dataSets and all dataSets the given <code>user</code> has read-access to.
   *
   * @param user the user that wants read-access, may be <code>null</code>
   * @return all published dataSets + all dataSets the given <code>user</code> has read-access to
   */
  public Collection<DataSet> getDataSetsWithReadAccess(@Nullable User user) {
    List<DataSet> dataSetsWithReadAccess = new ArrayList<>();
    for (Map<String, DataSet> userDataSets : dataSetMap.values()) {
      for (DataSet dataSet : userDataSets.values()) {
        try {
          if (permissionFetcher.hasPermission(user, dataSet.getMetadata(), READ)) {
            dataSetsWithReadAccess.add(dataSet);
          }
        } catch (PermissionFetchingException e) {
          LOG.error("Could not fetch read permission", e);
        }
      }
    }
    return dataSetsWithReadAccess;
  }

  public void removeDataSet(String ownerId, String dataSetName, User user)
      throws IOException, NotEnoughPermissionsException, DataSetDoesNotExistException {
    try {
      DataSet dataSet = dataSetMap.get(ownerId).get(dataSetName);
      if (dataSet == null) {
        LOG.warn("DataSet '{}' of user with id '{}' does not exist (anymore).", dataSetName, ownerId);
        throw new DataSetDoesNotExistException(dataSetName, ownerId);
      }
      String combinedId = dataSet.getMetadata().getCombinedId();
      if (!permissionFetcher.hasPermission(user, dataSet.getMetadata(), Permission.REMOVE_DATASET)) {
        throw new NotEnoughPermissionsException(
            String.format(
                "User '%s' is not allowed to remove dataset '%s'",
                user.getDisplayName(),
                combinedId
            )
        );
      }
      dataSetMap.get(ownerId).remove(dataSetName);
      dataSet.stop();
      permissionFetcher.removeAuthorizations(combinedId);
    } catch (PermissionFetchingException e) {
      throw new IOException(e);
    }

    // remove folder
    dataStorage.getDataSetStorage(ownerId, dataSetName).clear();
  }

  public void stop() {
    for (DataSet dataSet : getDataSets()) {
      dataSet.stop();
    }

    dataStoreFactory.stop();
  }

  public void start() throws IOException {
    dataStoreFactory.start();
    loadDataSetsFromJson();
  }

  public void subscribeToDataSetsUpdated(Runnable dataSetsUpdatedListener) {
    this.dataSetsUpdatedListeners.add(dataSetsUpdatedListener);
    this.getDataSets().forEach(dataSet -> dataSet.subscribeToDataChanges(dataSetsUpdatedListener));
  }

  public class DataSetDoesNotExistException extends Exception {
    DataSetDoesNotExistException(String dataSetName, String ownerId) {
      super(String.format("DataSet '%s' of user with id '%s' does not exist", dataSetName, ownerId));
    }
  }
}
