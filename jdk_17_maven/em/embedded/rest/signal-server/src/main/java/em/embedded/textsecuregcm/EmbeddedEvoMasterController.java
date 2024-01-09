package em.embedded.textsecuregcm;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.sql.DbSpecification;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.whispersystems.textsecuregcm.WhisperServerService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private static final int DYNAMODB_PORT = 8000;

    private static final String DYNAMODB_VERSION = "1.25.0";

    private static final GenericContainer dynamoDBContainer = new GenericContainer("amazon/dynamodb-local:" + DYNAMODB_VERSION)
            .withExposedPorts(DYNAMODB_PORT);

    private static final int REDIS_PORT = 6379;

    private static final String REDIS_VERSION = "7.2.3";

    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:" + REDIS_VERSION);

    private static final GenericContainer redisContainer = new GenericContainer(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT)
            .withEnv("ALLOW_EMPTY_PASSWORD", "yes")
            .withEnv("REDIS_NODES", "redis-cluster-01")
            .withCopyFileToContainer(MountableFile.forHostPath("src/main/resources/redis.conf"), "/usr/local/etc/redis/redis.conf")
            .withCommand("redis-server /usr/local/etc/redis/redis.conf");

    public static void main(String[] args) {

        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }

    private WhisperServerService application;

    @Override
    public boolean isSutRunning() {
        if (application == null) {
            return false;
        }

        return application.getJettyServer().isRunning();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.whispersystems.textsecuregcm.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://0.0.0.0:" + application.getJettyPort() + "/assets/swagger.json",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public String startSut() {
        System.setProperty("aws.region", "us-west-2");

        redisContainer.start();

        dynamoDBContainer.start();

        application = new WhisperServerService();

        //Dirty hack for DW...
        System.setProperty("dw.server.applicationConnectors[0].port", "0");
        System.setProperty("dw.server.adminConnectors[0].port", "0");

        System.setProperty("dw.cacheCluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.clientPresenceCluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.pubsub.uri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.pushSchedulerCluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.rateLimitersCluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.messageCache.cluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");
        System.setProperty("dw.metricsCluster.configurationUri", "redis://0.0.0.0:" + redisContainer.getFirstMappedPort() + "/");

        try {
            application.run("server", "src/main/resources/em-sample.yml");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {

        }

        while(!application.getJettyServer().isStarted()) {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {

            }
        }

        return "http://localhost:" + application.getJettyPort();
    }

    @Override
    public void stopSut() {
        if (application != null) {
            try {
                application.getJettyServer().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redisContainer.stop();
        dynamoDBContainer.stop();
    }

    @Override
    public void resetStateOfSUT() {
        JedisPool jedisPool = new JedisPool("0.0.0.0", redisContainer.getFirstMappedPort());

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
        }
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }

}
