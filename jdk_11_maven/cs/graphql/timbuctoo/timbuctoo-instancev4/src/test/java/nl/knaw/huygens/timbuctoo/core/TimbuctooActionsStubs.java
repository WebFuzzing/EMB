package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperationsStubs;
import nl.knaw.huygens.timbuctoo.experimental.womenwriters.WomenWritersJsonCrudServiceTest;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;

import java.net.URI;
import java.time.Clock;

import static org.mockito.Mockito.mock;

public class TimbuctooActionsStubs {

  public static TimbuctooActions withDataStore(DataStoreOperations dataStoreOperations) {
    return new TimbuctooActions(
      mock(PermissionFetcher.class),
      Clock.systemDefaultZone(),
      mock(RedirectionService.class),
      (coll, id, rev) -> URI.create("http://example.org/persistent"),
      dataStoreOperations,
      new AfterSuccessTaskExecutor()
    );
  }

  public static TimbuctooActions withDataStoreAndAfterSucces(DataStoreOperations dataStoreOperations,
                                                             AfterSuccessTaskExecutor afterSuccessTaskExecutor) {
    return new TimbuctooActions(
      mock(PermissionFetcher.class),
      Clock.systemDefaultZone(),
      mock(RedirectionService.class),
      (coll, id, rev) -> URI.create("http://example.org/persistent"),
      dataStoreOperations,
      afterSuccessTaskExecutor
    );
  }

  /**
   * Currently only used by {@link WomenWritersJsonCrudServiceTest}
   *
   * @deprecated mock TimbuctooActions or create one with one of the other stubs.
   */
  @Deprecated
  public static TimbuctooActions forGraphWrapper(TinkerPopGraphManager graphManager) {
    return new TimbuctooActions(
      mock(PermissionFetcher.class),
      Clock.systemDefaultZone(),
      mock(RedirectionService.class),
      (coll, id, rev) -> URI.create("http://example.org/persistent"),
      TinkerPopOperationsStubs.forGraphWrapper(graphManager),
      new AfterSuccessTaskExecutor()
    );
  }


}
