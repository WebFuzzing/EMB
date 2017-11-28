package em.external.org.devgateway.ocvn;

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
import org.evomaster.clientJava.controller.ExternalSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controller.db.DbCleaner;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ExternalEvoMasterController extends ExternalSutController {

    public static void main(String[] args) {

        /*
            FIXME: until SLF4J issue is fixed in pom, need to use
            -Devomaster.instrumentation.jar.path=<path>

            eg

            java -Devomaster.instrumentation.jar.path=/Users/foo/WEB/EvoMaster/client-java/instrumentation/target/evomaster-client-java-instrumentation-0.0.3-SNAPSHOT.jar  -jar em/external/rest/ocvn/target/ocvn-evomaster-runner.jar
         */

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
            jarLocation += "/web-1.1.1-SNAPSHOT-exec.jar";
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
//        url += ":derby://localhost/./temp/tmp_ocvn/" + derbyName;
//        url += ":derby://localhost//" + derbyName;

        return url;
    }

    @Override
    public String[] getJVMParameters() {

        /*
            FIXME

            see http://p6spy.github.io/p6spy/2.0/install.html#generic

            and class

            org.devgateway.toolkit.persistence.spring.DatabaseConfiguration

            look at classes with
            @Profile("integration")  and @Profile("!integration")

            don't manage to get P6Spy working due to

            Caused by: java.sql.SQLException: Unable to find a driver that accepts jdbc:derby://localhost:12347/./temp/tmp_ocvn/derby_12347;create=true
         */

        return new String[]{
                "-Dliquibase.enabled=false",
                "-Dspring.profiles.active=integration",
                "-Dspring.data.mongodb.port=" + mongodPort,
                "-Dspring.data.mongodb.uri=mongodb://localhost:"+mongodPort+"/ocvn",
                "-Dspring.datasource.driver-class-name=" + getDatabaseDriverName(),
                "-Dspring.datasource.url=" + dbUrl(false) + ";create=true",
//                "-Dspring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
//                "-Dspring.datasource.url=" + dbUrl(true) + ";create=true",
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

            NetworkServerControl nsc = new NetworkServerControl(InetAddress.getByName("localhost"), derbyPort);
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
        //TODO mongo

        //schema name takes value of "user"
//        DbCleaner.clearDatabase_Derby(connection, "app");
        DbCleaner.clearDatabase_Derby(connection, derbyName);
    }

    @Override
    public String getUrlOfSwaggerJSON() {
        return getBaseURL() + "/v2/api-docs?group=1ocDashboardsApi";
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

    @Override
    public List<String> getEndpointsToSkip() {
        return null;
    }
}
