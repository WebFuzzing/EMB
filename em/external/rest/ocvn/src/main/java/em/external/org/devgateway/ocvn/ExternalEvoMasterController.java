package em.external.org.devgateway.ocvn;


import com.mongodb.MongoClient;
import com.p6spy.engine.spy.P6SpyDriver;
import org.apache.derby.drda.NetworkServerControl;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.testcontainers.containers.GenericContainer;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        String jarLocation = "cs/rest-gui/ocvn/web/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/ocvn-rest-sut.jar";
        }

        int timeoutSeconds = 120;
        if(args.length > 3){
            timeoutSeconds = Integer.parseInt(args[3]);
        }

        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation, sutPort, timeoutSeconds);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    private final int timeoutSeconds;
    private final int sutPort;
    private final int derbyPort;
    private final String jarLocation;
    private Connection connection;
    private final String derbyName;
    private final String derbyDriver;

    private MongoClient mongoClient;

    private static final GenericContainer mongodb = new GenericContainer("mongo:3.2")
            .withExposedPorts(27017);

    public ExternalEvoMasterController() {
        this(40100, "../web/target/web-1.1.1-SNAPSHOT-exec.jar", 12345, 120);
    }

    public ExternalEvoMasterController(int controllerPort, String jarLocation, int sutPort, int timeoutSeconds) {
        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
        this.derbyPort = sutPort + 2;
        this.derbyName = "derby_" + derbyPort;
        this.derbyDriver = "org.apache.derby.jdbc.ClientDriver";
//        this.derbyDriver = "org.apache.derby.jdbc.ClientDriver40";
    }

    @Override
    public String[] getInputParameters() {
        return new String[]{"--server.port=" + sutPort};
    }


    private String dbUrl(boolean withP6Spy) {

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":derby://localhost:" + derbyPort + "/./temp/tmp_ocvn/" + derbyName;

        return url;
    }

    @Override
    public String[] getJVMParameters() {


        return new String[]{
                "-Dliquibase.enabled=false",
                "-Dspring.data.mongodb.uri=mongodb://"+mongodb.getContainerIpAddress()+":"+mongodb.getMappedPort(27017)+"/ocvn",
                "-Dspring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
                "-Dspring.datasource.url=" + dbUrl(true) + ";create=true",
                "-Dspring.datasource.username=app",
                "-Dspring.datasource.password=app",
                "-Ddg-toolkit.derby.port="+derbyPort,
                "-Dspring.cache.type=NONE"
        };
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
        return "Started WebApplication in ";
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return timeoutSeconds;
    }

    @Override
    public void preStart() {

        mongodb.start();

        try {
            mongoClient = new MongoClient(mongodb.getContainerIpAddress(),
                    mongodb.getMappedPort(27017));

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postStart() {
        try {
            Class.forName(derbyDriver);
            connection = DriverManager.getConnection(dbUrl(false), "app", "app");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        resetStateOfSUT();
    }

    @Override
    public void preStop() {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void postStop() {
        mongodb.stop();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.devgateway.ocvn.";
    }


    public void resetStateOfSUT() {
        mongoClient.getDatabase("ocvn").drop();
        mongoClient.getDatabase("ocvn-shadow").drop();

        //TODO will need to create user id/password
        DbCleaner.clearDatabase_Derby(connection, derbyName);
    }



    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                getBaseURL() + "/v2/api-docs?group=1ocDashboardsApi",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        //TODO need to handle form-based login
        return null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getDatabaseDriverName() {
        return derbyDriver;
    }


}
