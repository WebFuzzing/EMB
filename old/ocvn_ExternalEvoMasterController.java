package em.external.org.devgateway.ocvn;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import com.p6spy.engine.spy.P6SpyDriver;
import org.apache.derby.drda.NetworkServerControl;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;

import java.io.PrintWriter;
import java.net.InetAddress;
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
        String jarLocation = "cs/rest/original/ocvn/web/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/ocvn-sut.jar";
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
    private final int mongodPort;
    private final int derbyPort;
    private final String jarLocation;
    private MongodExecutable mongodExecutable;
    private MongoClient mongoClient;
    private Connection connection;
    private final String derbyName;
    private final String derbyDriver;
    private NetworkServerControl nsc;

    public ExternalEvoMasterController() {
        this(40100, "../web/target/web-1.1.1-SNAPSHOT-exec.jar", 12345, 120);
    }

    public ExternalEvoMasterController(int controllerPort, String jarLocation, int sutPort, int timeoutSeconds) {
        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
        this.mongodPort = sutPort + 1;
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
                "-Dspring.profiles.active=integration",
                "-Dspring.data.mongodb.port=" + mongodPort,
                "-Dspring.data.mongodb.uri=mongodb://localhost:"+mongodPort+"/ocvn",
//                "-Dspring.datasource.driver-class-name=" + getDatabaseDriverName(),
//                "-Dspring.datasource.url=" + dbUrl(false) + ";create=true",
                "-Dspring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
                "-Dspring.datasource.url=" + dbUrl(true) + ";create=true",
                "-Dspring.datasource.username=app",
                "-Dspring.datasource.password=app",
                "-Ddg-toolkit.derby.port="+derbyPort
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

        MongodStarter starter = MongodStarter.getDefaultInstance();

        try {
            String bindIp = "localhost";

            Storage replication = new Storage("./temp/tmp_ocvn/mongodb_"+mongodPort,null,0);

            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.V3_4)
                    .net(new Net(bindIp, mongodPort, Network.localhostIsIPv6()))
                    .replication(replication)
                    .build();


            mongodExecutable = starter.prepare(mongodConfig);
            mongodExecutable.start();

            mongoClient = new MongoClient(bindIp, mongodPort);

            nsc = new NetworkServerControl(InetAddress.getByName("localhost"), derbyPort);
            nsc.start(new PrintWriter(java.lang.System.out, true));

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
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
        if(nsc != null){
            try {
                nsc.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.devgateway.ocvn.";
    }


    public void resetStateOfSUT() {
        mongoClient.getDatabase("ocvn").drop();

        //schema name takes value of "user"
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
