package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.core.dto.CreateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.CreateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.DataStream;
import nl.knaw.huygens.timbuctoo.core.dto.EntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableCreateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableEntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearchResult;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.CustomEntityProperties;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.CustomRelationProperties;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * This class is performs all the steps needed to save entities relations, etc.
 */
public class TimbuctooActions implements AutoCloseable {

  private final PermissionFetcher permissionFetcher;
  private final Clock clock;
  private final RedirectionService redirectionService;
  private final UrlGenerator uriToRedirectToFromPersistentUrls;
  private final DataStoreOperations dataStoreOperations;
  private final AfterSuccessTaskExecutor afterSuccessTaskExecutor;

  public TimbuctooActions(PermissionFetcher permissionFetcher, Clock clock, RedirectionService redirectionService,
                          UrlGenerator uriToRedirectToFromPersistentUrls, DataStoreOperations dataStoreOperations,
                          AfterSuccessTaskExecutor afterSuccessTaskExecutor) {
    this.permissionFetcher = permissionFetcher;
    this.clock = clock;
    this.redirectionService = redirectionService;
    this.uriToRedirectToFromPersistentUrls = uriToRedirectToFromPersistentUrls;
    this.dataStoreOperations = dataStoreOperations;
    this.afterSuccessTaskExecutor = afterSuccessTaskExecutor;
  }

  public UUID createEntity(Collection collection, Optional<Collection> baseCollection,
                           Iterable<TimProperty<?>> properties, User user)
    throws PermissionFetchingException, IOException {
    checkIfAllowedToWrite(user, collection);
    UUID id = UUID.randomUUID();
    Change created = createChange(user);
    CreateEntity createEntity = ImmutableCreateEntity.builder()
      .properties(properties)
      .id(id)
      .created(created)
      .build();

    dataStoreOperations.createEntity(collection, baseCollection, createEntity);

    afterSuccessTaskExecutor.addTask(
      new AddPersistentUrlTask(
        redirectionService,
        uriToRedirectToFromPersistentUrls.apply(collection.getCollectionName(), id, 1),
        ImmutableEntityLookup.builder()
          .rev(1)
          .timId(id)
          .collection(collection.getCollectionName())
          .build()
      )
    );

    return id;
  }

  public void replaceEntity(Collection collection, UpdateEntity updateEntity, User user)
    throws PermissionFetchingException, NotFoundException, AlreadyUpdatedException,
    IOException {
    checkIfAllowedToWrite(user, collection);

    updateEntity.setModified(createChange(user));

    int rev = dataStoreOperations.replaceEntity(collection, updateEntity);
    afterSuccessTaskExecutor.addTask(
      new AddPersistentUrlTask(
        redirectionService,
        uriToRedirectToFromPersistentUrls.apply(collection.getCollectionName(), updateEntity.getId(), rev),
        ImmutableEntityLookup.builder()
          .rev(rev)
          .timId(updateEntity.getId())
          .collection(collection.getCollectionName())
          .build()
      )
    );
  }
  //FIXME: when adding the new datamodel. We need to fix the persistent url generator. It now generates a url per
  // collection, but writes to a property that exists regardless of the collection. It also generates a new persistent
  // url after you have deleted an entity (which therefore always 404's)

  public void deleteEntity(Collection collection, UUID uuid, User user)
    throws PermissionFetchingException, NotFoundException {
    checkIfAllowedToWrite(user, collection);

    int rev = dataStoreOperations.deleteEntity(collection, uuid, createChange(user));


    afterSuccessTaskExecutor.addTask(
      new AddPersistentUrlTask(
        redirectionService,
        uriToRedirectToFromPersistentUrls.apply(collection.getCollectionName(), uuid, rev),
        ImmutableEntityLookup.builder()
          .rev(rev)
          .timId(uuid)
          .collection(collection.getCollectionName())
          .build()
      )
    );
  }

  /**
   * Only added for the admin task AddTypeToNeo4JVertexTask
   * Do not use this method anywhere else
   */
  public void addTypeToEntity(UUID id, Collection typeToAdd) throws NotFoundException {
    dataStoreOperations.addTypeToEntity(id, typeToAdd);
  }

  /**
   * Only added for the admin task MoveEdgesTask
   * Do not use this method anywhere else
   */
  public void moveEdges(int fromVertex, int toVertex) throws NotFoundException {
    dataStoreOperations.moveEdges(fromVertex, toVertex);
  }

  private Change createChange(User user) {
    Change change = new Change();
    change.setUserId(user.getId());
    change.setTimeStamp(clock.instant().toEpochMilli());
    return change;
  }

  private void checkIfAllowedToWrite(User user, Collection collection) throws
    PermissionFetchingException {
    if (!permissionFetcher.getOldPermissions(user, collection.getVreName()).contains(Permission.WRITE)) {
      throw new PermissionFetchingException("Write permission not pressent.");
    }
  }

  public ReadEntity getEntity(Collection collection, UUID id, Integer rev) throws NotFoundException {
    return getEntity(collection, id, rev,
      (entity, entityVertex) -> {
      }, (traversalSource, vre, target, relationRef) -> {
      });
  }

  public ReadEntity getEntity(Collection collection, UUID id, Integer rev,
                              CustomEntityProperties customEntityProps,
                              CustomRelationProperties customRelationProps) throws NotFoundException {
    return dataStoreOperations.getEntity(id, rev, collection, customEntityProps, customRelationProps);
  }

  public DataStream<ReadEntity> getCollection(Collection collection, int start, int rows,
                                              boolean withRelations, CustomEntityProperties entityProps,
                                              CustomRelationProperties relationProps) {
    return dataStoreOperations.getCollection(collection, start, rows, withRelations, entityProps, relationProps);
  }

  public List<QuickSearchResult> doQuickSearch(Collection collection, QuickSearch quickSearch, String keywordType,
                                               int limit) {
    if (collection.getAbstractType().equals("keyword")) {
      return dataStoreOperations.doKeywordQuickSearch(collection, keywordType, quickSearch, limit);
    }
    return dataStoreOperations.doQuickSearch(collection, quickSearch, limit);
  }

  public UUID createRelation(Collection collection, CreateRelation createRelation, User user)
    throws PermissionFetchingException, IOException {
    checkIfAllowedToWrite(user, collection);

    // TODO make this method determine the id of the relation
    // createRelation.setId(id);
    createRelation.setCreated(createChange(user));

    try {
      return dataStoreOperations.acceptRelation(collection, createRelation);
    } catch (RelationNotPossibleException e) {
      throw new IOException(e);
    }
  }


  public void replaceRelation(Collection collection, UpdateRelation updateRelation, User user)
    throws PermissionFetchingException, NotFoundException {
    checkIfAllowedToWrite(user, collection);

    updateRelation.setModified(createChange(user));

    dataStoreOperations.replaceRelation(collection, updateRelation);
  }


  public void addPid(URI pidUri, EntityLookup entityLookup) throws NotFoundException {
    //TODO: add checks for entityLookup properties
    dataStoreOperations.addPid(entityLookup.getTimId().get(), entityLookup.getRev().get(), pidUri); //no collection?
  }

  //================== Metadata ==================
  public Vres loadVres() {
    return dataStoreOperations.loadVres();
  }

  public Collection getCollectionMetadata(String collectionName) throws InvalidCollectionException {
    Vres vres = loadVres();
    Optional<Collection> collection = vres.getCollection(collectionName);

    return collection.orElseThrow(() -> new InvalidCollectionException(collectionName));
  }

  //================== Transaction methods ==================
  @Override
  public void close() {
    dataStoreOperations.close();
  }

  public void success() {
    dataStoreOperations.success();
  }

  public void rollback() {
    dataStoreOperations.rollback();
  }

  public Vre getVre(String vreName) {
    return loadVres().getVre(vreName);
  }



  public byte[] getVreImageBlob(String vreName) {
    return dataStoreOperations.getVreImageBlob(vreName);
  }


  //================== Inner classes ==================
  @FunctionalInterface
  public interface TimbuctooActionsFactory {
    TimbuctooActions create(AfterSuccessTaskExecutor afterSuccessTaskExecutor);
  }

  static class AddPersistentUrlTask implements AfterSuccessTaskExecutor.Task {
    private final RedirectionService redirectionService;
    private final URI uriToRedirectTo;
    private final EntityLookup entityLookup;

    public AddPersistentUrlTask(RedirectionService redirectionService, URI uriToRedirectTo,
                                EntityLookup entityLookup) {
      this.redirectionService = redirectionService;
      this.uriToRedirectTo = uriToRedirectTo;
      this.entityLookup = entityLookup;
    }

    @Override
    public void execute() throws Exception {
      redirectionService.oldAdd(uriToRedirectTo, entityLookup);
    }

    @Override
    public String getDescription() {
      return String.format("Add handle to '%s'", entityLookup);
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }
  }

  public static class TimbuctooActionsFactoryImpl implements TimbuctooActionsFactory {
    private final PermissionFetcher permissionFetcher;
    private final Clock clock;
    private final RedirectionService redirectionService;
    private final UrlGenerator uriToRedirectToFromPersistentUrls;
    private final Supplier<DataStoreOperations> dataStoreOperationsSupplier;

    public TimbuctooActionsFactoryImpl(PermissionFetcher permissionFetcher, Clock clock,
                                       RedirectionService redirectionService,
                                       UrlGenerator uriToRedirectToFromPersistentUrls,
                                       Supplier<DataStoreOperations> dataStoreOperationsSupplier) {
      this.permissionFetcher = permissionFetcher;
      this.clock = clock;
      this.redirectionService = redirectionService;
      this.uriToRedirectToFromPersistentUrls = uriToRedirectToFromPersistentUrls;
      this.dataStoreOperationsSupplier = dataStoreOperationsSupplier;
    }

    @Override
    public TimbuctooActions create(AfterSuccessTaskExecutor afterSuccessTaskExecutor) {
      return new TimbuctooActions(
        permissionFetcher,
        clock,
        redirectionService,
        uriToRedirectToFromPersistentUrls,
        dataStoreOperationsSupplier.get(),
        afterSuccessTaskExecutor
      );
    }
  }
}

