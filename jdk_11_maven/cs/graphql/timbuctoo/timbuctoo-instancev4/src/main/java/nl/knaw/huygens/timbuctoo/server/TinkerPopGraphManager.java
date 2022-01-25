package nl.knaw.huygens.timbuctoo.server;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.Lists;
import io.dropwizard.lifecycle.Managed;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopConfig;
import nl.knaw.huygens.timbuctoo.server.databasemigration.DatabaseMigration;
import nl.knaw.huygens.timbuctoo.server.databasemigration.DatabaseMigrator;
import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration.SubgraphStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.cluster.ClusterSettings;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.kernel.ha.HaSettings;
import org.neo4j.kernel.ha.HighlyAvailableGraphDatabase;
import org.neo4j.kernel.ha.cluster.member.ClusterMembers;
import org.neo4j.logging.slf4j.Slf4jLogProvider;
import org.neo4j.tinkerpop.api.impl.Neo4jGraphAPIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;

public class TinkerPopGraphManager extends HealthCheck implements Managed, GraphWrapper, GraphWaiter {
  private static final SubgraphStrategy LATEST_ELEMENTS =
          SubgraphStrategy.build().edgeCriterion(has("isLatest", true)).vertexCriterion(has("isLatest", true)).create();

  private static final Logger LOG = LoggerFactory.getLogger(TimbuctooV4.class);

  private String lastHealthCheckLog = "";
  private final TinkerPopConfig configuration;
  private final LinkedHashMap<String, DatabaseMigration> migrations;
  protected final List<Consumer<Graph>> graphWaitList;

  private File databasePath;

  protected Neo4jGraph graph;
  protected GraphDatabaseService graphDatabase;

  public TinkerPopGraphManager(TinkerPopConfig configuration,
                               LinkedHashMap<String, DatabaseMigration> migrations) {
    this.configuration = configuration;
    graphWaitList = Lists.newArrayList();

    this.migrations = migrations;
  }

  @Override
  public void start() throws Exception {
    synchronized (graphWaitList) {
      initGraphDatabaseService();
      this.graph = Neo4jGraph.open(new Neo4jGraphAPIImpl(graphDatabase));
      new DatabaseMigrator(this, migrations).execute();
      callWaiters();
    }
  }

  protected void callWaiters() {
    AtomicInteger counter = new AtomicInteger(0);
    graphWaitList.forEach(consumer -> {
      try {
        consumer.accept(graph);
      } catch (RuntimeException e) {
        LOG.error(e.getMessage(), e);
      } finally {
        if (graph.tx().isOpen()) {
          LOG.error("Unclosed transaction at " + counter.get());
          graph.tx().close();
        }
        counter.incrementAndGet();
      }
    });
  }

  protected void initGraphDatabaseService() {
    if (databasePath == null) {
      databasePath = new File(configuration.getDatabasePath());
    }
    if (graphDatabase == null) {
      if (configuration.hasHaconfig()) {
        TinkerPopConfig.HaConfig haconfig = configuration.getHaconfig();
        LOG.info(
          "Launching HA mode. Server id is " +
          haconfig.getServerId() +
          " database is at " +
          databasePath.getAbsolutePath() +
          ". allow init cluster is " +
          haconfig.allowInitCluster()
        );
        final GraphDatabaseBuilder graphDatabaseBuilder =
          new HighlyAvailableGraphDatabaseFactory()
            .setUserLogProvider(new Slf4jLogProvider())
            .newEmbeddedDatabaseBuilder(databasePath)
            .setConfig(GraphDatabaseSettings.allow_store_upgrade, "true")

            .setConfig(ClusterSettings.allow_init_cluster, haconfig.allowInitCluster())
            .setConfig(ClusterSettings.server_id, haconfig.getServerId())
            .setConfig(ClusterSettings.initial_hosts, haconfig.getInitialHosts())
            .setConfig(ClusterSettings.cluster_server, haconfig.getIp() + ":5001")
            .setConfig(HaSettings.ha_server, haconfig.getIp() + ":6001")
            /*
             * Neo4j synchronizes the slave databases via pulls of the master data. By default this property is not
             * activated (set to 0s). So this property has to be set. An alternative is to set 'ha.tx_push_factor'.
             * Since a network connection might be temporarily down, a pull is safer then a push. The push_factor is
             * meant for ensuring data duplication so that a master can safely crash
             */
            .setConfig(HaSettings.pull_interval, haconfig.getPullInterval())
            .setConfig(HaSettings.tx_push_factor, haconfig.getPushFactor());
        if (configuration.getPageCacheMemory().length() > 0) {
          graphDatabaseBuilder.setConfig(GraphDatabaseSettings.pagecache_memory, configuration.getPageCacheMemory());
        }
        graphDatabase = graphDatabaseBuilder.newGraphDatabase();
      } else {
        LOG.info("Launching local non-ha mode. Database at " + databasePath.getAbsolutePath());
        graphDatabase = new GraphDatabaseFactory()
          .setUserLogProvider( new Slf4jLogProvider() )
          .newEmbeddedDatabaseBuilder(databasePath)
          .setConfig(GraphDatabaseSettings.allow_store_upgrade, "true")
          .newGraphDatabase();
      }
    }
  }

  @Override
  public void stop() throws Exception {
    LOG.info("Stopping database");
    graph.close();
    graphDatabase.shutdown();
    LOG.info("Database stopped");
  }

  @Override
  protected Result check() throws Exception {
    StringBuilder logMessage = new StringBuilder();
    if (graphDatabase instanceof HighlyAvailableGraphDatabase) {
      HighlyAvailableGraphDatabase haDb = (HighlyAvailableGraphDatabase) graphDatabase;
      logMessage
        .append("From the perspective of ")
        .append(configuration.getHaconfig().getServerId())
        .append(":\n");
      haDb.getDependencyResolver().resolveDependency(ClusterMembers.class).getMembers().forEach(member -> {
        logMessage.append("  member ").append(member.getInstanceId()).append(": ")
          .append("alive=").append(member.isAlive()).append(", ");
        if (member.getStoreId() != null) {
          logMessage.append("storeVersion=").append(member.getStoreId().getStoreVersion()).append(", ");
        }
        if (member.getRoles() != null) {
          logMessage.append("roles=");
          member.getRoles().forEach(role -> {
            logMessage.append(role).append("; ");
          });
        }
        logMessage.append("\n");
      });
      String logMessageStr = logMessage.toString();
      if (!lastHealthCheckLog.equals(logMessageStr)) {
        lastHealthCheckLog = logMessageStr;
        LOG.info(logMessageStr);
      }
    }

    /*
     * TODO find a better way to check the database is available.
     * isAvailable only checks for HA availability and whether it is shutdown. It will return true even if the database
     * files have been removed and each write will fail.
     * Trying to retrieve nodes from the non-existing database does not result in an Exception.
     * A commit() with a write will fail, but is not a test that you might want to run every second.
     */
    if (graphDatabase.isAvailable(1000)) {
      if (databasePath.exists()) {
        return Result.healthy(logMessage.toString());
      }
      return Result.unhealthy("Path to database [%s] does not exist", databasePath);
    }

    return Result.unhealthy("Database is unavailable.");
  }

  @Override
  public Graph getGraph() {
    return this.graph;
  }

  @Override
  public void onGraph(Consumer<Graph> graphConsumer) {
    synchronized (graphWaitList) {
      if (graph == null) {
        graphWaitList.add(graphConsumer);
      } else {
        graphConsumer.accept(graph);
      }
    }
  }

  @Override
  public GraphTraversalSource getLatestState() {
    return GraphTraversalSource.build().with(LATEST_ELEMENTS).create(graph);
  }


  @Override
  public GraphTraversal<Vertex, Vertex> getCurrentEntitiesFor(String... entityTypeNames) {
    if (entityTypeNames.length == 1) {
      String type = entityTypeNames[0];
      return getLatestState().V().has(T.label, LabelP.of(type));
    } else {
      P<String> labels = LabelP.of(entityTypeNames[0]);
      for (int i = 1; i < entityTypeNames.length; i++) {
        labels = labels.or(LabelP.of(entityTypeNames[i]));
      }

      return getLatestState().V().has(T.label, labels);
    }
  }

  public GraphDatabaseService getGraphDatabase() {
    initGraphDatabaseService();
    return graphDatabase;
  }
}
