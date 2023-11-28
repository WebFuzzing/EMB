package em.embedded.reservationsapi;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.JsonTokenPostLoginDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import sk.cyrilgavala.reservationsApi.ReservationsApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Class used to start/stop the SUT. This will be controller by the EvoMaster process
 */
public class EmbeddedEvoMasterController extends EmbeddedSutController {

    public static void main(String[] args) {

        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private ConfigurableApplicationContext ctx;

    private static final int MONGODB_PORT = 27017;

    //https://www.mongodb.com/docs/drivers/java/sync/current/compatibility/
    private static final String MONGODB_VERSION = "4.4";

    private static final String MONGODB_DATABASE_NAME = "reservations-api";

    //  docker run -p 27017:27017  -e MONGODB_REPLICA_SET_MODE=primary -e  ALLOW_EMPTY_PASSWORD=yes bitnami/mongodb:4.4
    //  /bitnami/mongodb
    //  https://hub.docker.com/r/bitnami/mongodb

    private static final GenericContainer mongodbContainer = new GenericContainer("bitnami/mongodb:" + MONGODB_VERSION)
            .withTmpFs(Collections.singletonMap("/bitnami/mongodb", "rw"))
            .withEnv("MONGODB_REPLICA_SET_MODE","primary")
            .withEnv("ALLOW_EMPTY_PASSWORD","yes")
            .withExposedPorts(MONGODB_PORT);

//    private static final GenericContainer mongodbContainer = new GenericContainer("mongo:" + MONGODB_VERSION)
//            .withTmpFs(Collections.singletonMap("/data/db", "rw"))
//            .withExposedPorts(MONGODB_PORT);

    private String mongoDbUrl;

    private MongoClient mongoClient;

    public EmbeddedEvoMasterController() {
        this(0);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }


    @Override
    public String startSut() {

        mongodbContainer.start();
        mongoDbUrl = "mongodb://" + mongodbContainer.getContainerIpAddress() + ":" + mongodbContainer.getMappedPort(MONGODB_PORT) + "/" + MONGODB_DATABASE_NAME;
        mongoClient = MongoClients.create(mongoDbUrl);

        //from command line:
        //--databaseUrl=mongodb://localhost:27017/Reservations --app.jwt.secret=abcdef0123456789

        ctx = SpringApplication.run(ReservationsApi.class,
                new String[]{"--server.port=0",
                        "--databaseUrl="+mongoDbUrl,
                        "--spring.data.mongodb.uri="+mongoDbUrl,
                        "--spring.cache.type=NONE",
                        "--app.jwt.secret=abcdef012345678901234567890123456789abcdef012345678901234567890123456789"
                });

        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }


    @Override
    public boolean isSutRunning() {
        return ctx != null && ctx.isRunning();
    }

    @Override
    public void stopSut() {
        ctx.stop();
        ctx.close();

        mongodbContainer.stop();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "sk.cyrilgavala.reservationsApi.";
    }

    @Override
    public void resetStateOfSUT() {
        mongoClient.getDatabase(MONGODB_DATABASE_NAME).drop();



        // "bar123"
        // YmFyMTIz
        // $2a$10$b/SjlT3jexPDGci3EtmzpOnYwmjXrtzCQq5dn8rbMCgz7UZ/saylm
        mongoClient.getDatabase(MONGODB_DATABASE_NAME).createCollection("users");

        MongoCollection<Document> users = mongoClient.getDatabase(MONGODB_DATABASE_NAME).getCollection("users");
        users.insertMany(Arrays.asList(
                new Document()
                        .append("_id", new ObjectId())
                        .append("_class", "sk.cyrilgavala.reservationsApi.model.User")
                        .append("username", "foo")
                        .append("email", "foo@foo.com")
                        .append("password", "$2a$10$b/SjlT3jexPDGci3EtmzpOnYwmjXrtzCQq5dn8rbMCgz7UZ/saylm")
                        .append("role", "USER"),
                new Document()
                        .append("_id", new ObjectId())
                        .append("_class", "sk.cyrilgavala.reservationsApi.model.User")
                        .append("username", "bar")
                        .append("email", "bar@foo.com")
                        .append("password", "$2a$10$b/SjlT3jexPDGci3EtmzpOnYwmjXrtzCQq5dn8rbMCgz7UZ/saylm")
                        .append("role", "USER"),
                new Document()
                        .append("_id", new ObjectId())
                        .append("_class", "sk.cyrilgavala.reservationsApi.model.User")
                        .append("username", "admin")
                        .append("email", "admin@foo.com")
                        .append("password","$2a$10$nEDY5j731yXGnQHyM39PWurJWr1FukegmKYYarK5WOoAMmgDs6D3u")
                        //.append("password", "$2a$10$b/SjlT3jexPDGci3EtmzpOnYwmjXrtzCQq5dn8rbMCgz7UZ/saylm")
                        .append("role", "ADMIN")
        ));


    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }


    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {

        return Arrays.asList(
            new AuthenticationDto() {{
                name = "admin";
                jsonTokenPostLogin = new JsonTokenPostLoginDto(){{
                    userId = "admin";
                    endpoint ="/api/user/login";
                    jsonPayload = "{\"username\":\"admin\", \"password\":\"bar123\"}";
                    extractTokenField ="/accessToken";
                    headerPrefix = "Bearer ";
                }};
                }}
        );
    }




    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/v3/api-docs",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public Object getMongoConnection() {return mongoClient;}

}
