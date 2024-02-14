package em.embedded.familie.ba.sak;

import no.nav.familie.ba.sak.ApplicationKt;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.sql.DbCleaner;
import org.evomaster.client.java.sql.DbSpecification;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private static final String POSTGRES_VERSION = "13.13";

    private static final String POSTGRES_PASSWORD = "password";

    private static final int POSTGRES_PORT = 5432;

    private static final GenericContainer postgresContainer = new GenericContainer("postgres:" + POSTGRES_VERSION)
            .withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD)
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust") //to allow all connections without a password
            .withEnv("POSTGRES_DB", "familiebasak")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"))
            .withExposedPorts(POSTGRES_PORT);

    private ConfigurableApplicationContext ctx;

    private Connection sqlConnection;
    private List<DbSpecification> dbSpecification;

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }


    public static void main(String[] args) {
        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    @Override
    public boolean isSutRunning() {
        return ctx!=null && ctx.isRunning();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "no.nav.familie.ba.sak.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        //TODO seems like it uses auth
        return null;
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
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public String startSut() {
        postgresContainer.start();

        String postgresURL = "jdbc:postgresql://" + postgresContainer.getHost() + ":" + postgresContainer.getMappedPort(POSTGRES_PORT) + "/familiebasak";

        //TODO should go through all the environment variables in application properties
        System.setProperty("AZUREAD_TOKEN_ENDPOINT_URL","http://foo");
        System.setProperty("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT","bar");
        System.setProperty("AZURE_APP_CLIENT_ID","bar");
        System.setProperty("NAIS_APP_NAME","bar");
        System.setProperty("UNLEASH_SERVER_API_URL","http://bar");
        System.setProperty("UNLEASH_SERVER_API_TOKEN","bar");


        ctx = SpringApplication.run(no.nav.familie.ba.sak.FamilieBaSakApplication.class, new String[]{
                "--server.port=0",
                "--spring.profiles.active=dev",
                "--management.server.port=-1",
                "--server.ssl.enabled=false",
                "--spring.datasource.url=" + postgresURL,
                "--spring.datasource.username=postgres",
                "--spring.datasource.password=" + POSTGRES_PASSWORD,
                "--sentry.logging.enabled=false",
                "--sentry.environment=local",
                //TODO check when dealing with Kafka
                "--funksjonsbrytere.kafka.producer.enabled=false",
                "--funksjonsbrytere.enabled=false",
                "--logging.level.root=OFF",
                "--logging.config=classpath:logback.xml",
                //"--logback.configurationFile=src/main/resources/logback.xml",
                "--logging.level.org.springframework=INFO",
                //"--API_SCOPE=api://AZURE_APP_CLIENT_ID/.default"
               // "--spring.main.web-application-type=none"
        });


        // https://www.baeldung.com/spring-boot-application-context-exception
        // spring.main.web-application-type=none

        if (sqlConnection != null) {
            try {
                sqlConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //JdbcTemplate jdbc = ctx.getBean(JdbcTemplate.class);
        try {
            sqlConnection = DriverManager.getConnection(postgresURL, "postgres", POSTGRES_PASSWORD);
            //jdbc.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        dbSpecification = Arrays.asList(new DbSpecification(DatabaseType.POSTGRES, sqlConnection));

        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
    //    return ctx.getEnvironment().getProperty("server.port", Integer.class);
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }

    @Override
    public void stopSut() {
        postgresContainer.stop();
        if(ctx!=null)ctx.stop();
    }

    @Override
    public void resetStateOfSUT() {
      //  DbCleaner.clearDatabase(sqlConnection, List.of(), DatabaseType.POSTGRES);
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return dbSpecification;
    }
}
