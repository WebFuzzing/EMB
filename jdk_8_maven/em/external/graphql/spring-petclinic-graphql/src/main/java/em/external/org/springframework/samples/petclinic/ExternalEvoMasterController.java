package em.external.org.springframework.samples.petclinic;

import com.p6spy.engine.spy.P6SpyDriver;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.db.SqlScriptRunnerCached;
import org.evomaster.client.java.controller.problem.GraphQlProblem;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
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
        String jarLocation = "cs/graphql/spring-petclinic-graphql/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/petclinic-sut.jar";
        }

        int timeoutSeconds = 120;
        if(args.length > 3){
            timeoutSeconds = Integer.parseInt(args[3]);
        }

        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation,
                        sutPort, timeoutSeconds);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    private final int timeoutSeconds;
    private final int sutPort;
    private final String jarLocation;
    private Connection connection;

    private static final GenericContainer postgres = new GenericContainer("postgres:9")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "petclinic")
            .withEnv("POSTGRES_HOST_AUTH_METHOD","trust")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    public ExternalEvoMasterController(){
        this(40100, "../core/target", 12345, 120);
    }

    public ExternalEvoMasterController(
            int controllerPort, String jarLocation, int sutPort, int timeoutSeconds
           ) {

        if(jarLocation==null || jarLocation.isEmpty()){
            throw new IllegalArgumentException("Missing jar location");
        }


        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
    }


    @Override
    public String[] getInputParameters() {
        return new String[]{"--server.port=" + sutPort};
    }

    public String[] getJVMParameters() {

        return new String[]{
                "-Dspring.datasource.url=" + dbUrl(true),
                "-Dspring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
                "-Dspring.cache.type=none",
                "-Dspring.profiles.active=postgresql,spring-data-jpa",
                "-Dspring.jmx.enabled=false",
        };
    }

    private String dbUrl(boolean withP6Spy) {

        String host = postgres.getContainerIpAddress();
        int port = postgres.getMappedPort(5432);

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":postgresql://"+host+":"+port+"/petclinic";

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
        return "Tomcat started on port";
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
            connection = DriverManager.getConnection(dbUrl(false), "postgres", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SqlScriptRunnerCached.runScriptFromResourceFile(connection,"/initDB.sql");
    }

    @Override
    public void resetStateOfSUT() {
        DbCleaner.clearDatabase_Postgres(connection);
        SqlScriptRunnerCached.runScriptFromResourceFile(connection,"/populateDB.sql");
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
        return "org.springframework.samples.petclinic";
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new GraphQlProblem("/graphql");
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
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
        return null;
    }
}
