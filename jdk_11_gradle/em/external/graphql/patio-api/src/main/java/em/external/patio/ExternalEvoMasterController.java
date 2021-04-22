package em.external.patio;

import com.p6spy.engine.spy.P6SpyDriver;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.JsonTokenPostLoginDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.db.SqlScriptRunnerCached;
import org.evomaster.client.java.controller.problem.GraphQlProblem;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ExternalEvoMasterController extends ExternalSutController {


    public static void main(String[] args) {

        int controllerPort = 40100;
        if (args.length > 0) {
            controllerPort = Integer.parseInt(args[0]);
        }
        int sutPort = 12345;
        if (args.length > 1) {
            sutPort = Integer.parseInt(args[1]);
        }
        String jarLocation = "cs/graphql/patio-api/build";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/patio-api-0.1.0-all.jar";
        }

        int timeoutSeconds = 120;
        if(args.length > 3){
            timeoutSeconds = Integer.parseInt(args[3]);
        }
        String command = "java";
        if(args.length > 4){
            command = args[4];
        }


        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation,
                        sutPort, timeoutSeconds, command);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    private final int timeoutSeconds;
    private final int sutPort;
    private final String jarLocation;
    private Connection connection;

    private static final GenericContainer postgres = new GenericContainer("postgres:9")
            .withEnv("POSTGRES_HOST_AUTH_METHOD","trust")
            .withEnv("POSTGRES_DB", "patio")
            .withEnv("POSTGRES_USER", "patio")
            .withEnv("POSTGRES_PASSWORD", "patio")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    public ExternalEvoMasterController(){
        this(40100, "../core/target", 12345, 120, "java");
    }

    public ExternalEvoMasterController(
            int controllerPort, String jarLocation, int sutPort, int timeoutSeconds, String command
           ) {

        if(jarLocation==null || jarLocation.isEmpty()){
            throw new IllegalArgumentException("Missing jar location");
        }


        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
        setJavaCommand(command);
    }


    @Override
    public String[] getInputParameters() {
        return new String[]{
                "-micronaut.server.port="+sutPort,
                "-datasources.default.url=" + dbUrl(true),
                "-datasources.default.driverClassName="+ P6SpyDriver.class.getName()
        };
    }

    public String[] getJVMParameters() {
        return new String[]{};
    }

    private String dbUrl(boolean withP6Spy) {

        String host = postgres.getContainerIpAddress();
        int port = postgres.getMappedPort(5432);

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":postgresql://"+host+":"+port+"/patio";

        return url;
    }

    @Override
    public String getBaseURL() {
        return "http://localhost:" + sutPort;
    }

    @Override
    public String getPathToExecutableJar() {
        return jarLocation;
    }

    @Override
    public String getLogMessageOfInitializedServer() {
        return "Tomcat started on port"; //FIXME
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return timeoutSeconds;
    }

    @Override
    public void preStart() {
        postgres.start();
    }

    @Override
    public void postStart() {
        closeDataBaseConnection();

        try {
            Class.forName(getDatabaseDriverName());
            connection = DriverManager.getConnection(dbUrl(false), "patio", "patio");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetStateOfSUT() {
        DbCleaner.clearDatabase_Postgres(connection, "public", List.of("flyway_schema_history"));
        SqlScriptRunnerCached.runScriptFromResourceFile(connection,"/initDB.sql");
    }

    @Override
    public void preStop() {
        closeDataBaseConnection();
    }

    @Override
    public void postStop() {
        postgres.stop();
    }

    private void closeDataBaseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "patio.";
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new GraphQlProblem("/graphql");
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getDatabaseDriverName() {
        return "org.postgresql.Driver";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {

        JsonTokenPostLoginDto token = new JsonTokenPostLoginDto();
        token.userId = "Stark";
        token.headerPrefix = "JWT ";
        token.endpoint = "/graphql";
        token.jsonPayload = "{\"query\": \"{login(email: \\\"Tony Stark\\\",password: \\\"avengers\\\"){tokens{authenticationToken}}}\"}";
        token.extractTokenField = "/tokens/authenticationToken";

        AuthenticationDto dto = new AuthenticationDto("Stark");
        dto.jsonTokenPostLogin = token;

        return List.of(dto);
    }
}
