package em.external.org.ind0;

import com.p6spy.engine.spy.P6SpyDriver;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ExternalEvoMasterController extends ExternalSutController {

    /*
        All info that could possibly identify the case study is removed, and
        must be passed as either parameter input or environment variable
     */
    private static final String SUT_LOCATION_IND0 = System.getenv("SUT_LOCATION_IND0");
    private static final String SUT_PACKAGE_IND0 = System.getenv("SUT_PACKAGE_IND0");

    public static void main(String[] args) {

        int controllerPort = 40100;
        if (args.length > 0) {
            controllerPort = Integer.parseInt(args[0]);
        }
        int sutPort = 12345;
        if (args.length > 1) {
            sutPort = Integer.parseInt(args[1]);
        }
        String jarLocation = SUT_LOCATION_IND0;
        if (args.length > 2) {
            jarLocation = args[2];
        }

        int timeoutSeconds = 120;
        if(args.length > 3){
            timeoutSeconds = Integer.parseInt(args[3]);
        }

        String packagesToInstrument = SUT_PACKAGE_IND0;
        if(args.length > 4){
            packagesToInstrument = args[4];
        }

        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation,
                        sutPort, timeoutSeconds, packagesToInstrument);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    private final int timeoutSeconds;
    private final int sutPort;
    private final String jarLocation;
    private final String packagesToInstrument;
    private Connection connection;

    private static final GenericContainer postgres = new GenericContainer("postgres:9")
            .withExposedPorts(5432);

    public ExternalEvoMasterController(){
        this(40100, SUT_LOCATION_IND0, 12345, 120, SUT_PACKAGE_IND0);
    }

    public ExternalEvoMasterController(
            int controllerPort, String jarLocation, int sutPort, int timeoutSeconds,
            String packagesToInstrument) {

        if(jarLocation==null || jarLocation.isEmpty()){
            throw new IllegalArgumentException("Missing jar location");
        }
        if(packagesToInstrument==null || packagesToInstrument.isEmpty()){
            throw new IllegalArgumentException("Missing packages to instrument");
        }

        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        this.packagesToInstrument = packagesToInstrument;
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
                "-Dspring.datasource.username=postgres",
                "-Dspring.datasource.password",
                "-Dspring.jpa.show-sql=false",
                "-Dspring.cache.type=none",
                "-Dspring.jmx.enabled=false",
                "-Xmx4G"
        };
    }

    private String dbUrl(boolean withP6Spy) {

        String host = postgres.getContainerIpAddress();
        int port = postgres.getMappedPort(5432);

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":postgresql://"+host+":"+port+"/postgres?currentSchema=comments";

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
    }

    @Override
    public void resetStateOfSUT() {
        DbCleaner.clearDatabase_Postgres(connection,
                "comments",
                Arrays.asList("flyway_schema_history"));
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
        return packagesToInstrument;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                getBaseURL() + "/v2/api-docs",
                null
        );
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
