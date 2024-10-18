package em.embedded.bibliothek;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.papermc.bibliothek.BibliothekApplication;

import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;


import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import static java.time.temporal.ChronoUnit.SECONDS;


/**
 * Class used to start/stop the SUT. This will be controller by the EvoMaster process
 */
public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedEvoMasterController.class);

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

    private static final String MONGODB_VERSION = "6.0";

    private static final String MONGODB_DATABASE_NAME = "library";

    private static final GenericContainer mongodbContainer = new GenericContainer("mongo:" + MONGODB_VERSION)
            .withTmpFs(Collections.singletonMap("/data/db", "rw"))
            .withStartupTimeout(Duration.of(150, SECONDS))
            .withExposedPorts(MONGODB_PORT);

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



        ctx = SpringApplication.run(BibliothekApplication.class,
                new String[]{"--server.port=0",
                        "--databaseUrl="+mongoDbUrl,
                        "--spring.data.mongodb.uri="+mongoDbUrl,
                        "--spring.cache.type=NONE",
                        "--app.storagePath=./tmp/bibliothek"
                });

        // add two users to MongoDB database
        // TODO we need two POST requests here for adding two users.
        String sutURL = "http://localhost:" + getSutPort();

        if(this.addUserToMongo(sutURL, "user1", "pass1")) {
            log.info("Successfully added user1 to the database");
        }
        else {
            log.error("Failed to add user1 to the database");
        }

        if(this.addUserToMongo(sutURL, "user2", "pass2")) {
            log.info("Successfully added user2 to the database");
        }
        else {
            log.error("Failed to add user2 to the database");
        }

        return sutURL;
    }

    private boolean addUserToMongo(String sutURL, String username, String password) {

        String requestBody = "{\"username\": \"" + username +  "\", \"password\": \""+ password +"\" }";


        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(sutURL + "/createuser"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response != null) {
            if (response.statusCode() == 200) {
                return true;
            }
        }
        return false;
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
        return "io.papermc.bibliothek.";
    }

    @Override
    public void resetStateOfSUT() {
        //mongoClient.getDatabase(MONGODB_DATABASE_NAME).drop();
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }


    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        //TODO might need to setup JWT headers here

        AuthenticationDto dto1 = AuthUtils.getForJWT("userDto1", "/login", """
                                {\"username\":\"user1\",
                                \"password\":\"pass1\"}
                            """.trim(), "/accessToken");

        AuthenticationDto dto2 = AuthUtils.getForJWT("userDto2", "/login", """
                                {\"username\":\"user2\",
                                \"password\":\"pass2\"}
                            """.trim(), "/accessToken");

        List<AuthenticationDto> listOfDtos= new ArrayList<>();

        listOfDtos.add(dto1);
        listOfDtos.add(dto2);

        return listOfDtos;
    }




    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/openapi",
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
