package em.external.se.devscout.scoutapi;

import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType;
import org.evomaster.client.java.sql.DbCleaner;
import org.evomaster.client.java.sql.SqlScriptRunner;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.auth.HeaderDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.h2.tools.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
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
        String jarLocation = "cs/rest/original/scout-api/api/target";
        if(args.length > 2){
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/scout-api-sut.jar";
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
                new ExternalEvoMasterController(controllerPort, jarLocation, sutPort, timeoutSeconds, command);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private final int timeoutSeconds;
    private final int sutPort;
    private final int dbPort;
    private  String jarLocation;
    private final String tmpDir;
    private final String CONFIG_FILE = "scout_api_evomaster.yml";

    private Connection sqlConnection;
    private List<DbSpecification> dbSpecification;
//    private final List<String> sqlCommands;

    private String INIT_DB_SCRIPT_PATH = "/init_db.sql";

    private Server h2;


    public ExternalEvoMasterController(){
        this(40100, "../api/target", 12345, 120, "java");
    }

    public ExternalEvoMasterController(String jarLocation) {
        this();
        this.jarLocation = jarLocation;
    }

    public ExternalEvoMasterController(int controllerPort,
                                       String jarLocation,
                                       int sutPort,
                                       int timeoutSeconds,
                                       String command) {
        this.sutPort = sutPort;
        this.dbPort = sutPort + 1;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
        setJavaCommand(command);

        String base = Paths.get(jarLocation).toAbsolutePath().getParent().normalize().toString();
        tmpDir = base + "/temp/tmp_scout_api/temp_"+dbPort;
        createConfigurationFile();
    }

    private String dbUrl( ) {

        String url = "jdbc";
        url += ":h2:tcp://localhost:" + dbPort + "/mem:testdb_" + dbPort;

        return url;
    }

    /**
           Unfortunately, it seems like Dropwizard is buggy, and has
           problems with overriding params without a YML file :(
     */
    private void createConfigurationFile() {

        //save config to same folder of JAR file
        Path path = getConfigPath();

        try(InputStream is = this.getClass().getResourceAsStream("/"+ CONFIG_FILE )){
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Path getConfigPath(){
        return Paths.get(jarLocation)
                .toAbsolutePath()
                .getParent()
                .resolve(CONFIG_FILE)
                .normalize();
    }

    @Override
    public String[] getInputParameters() {
        return new String[]{"server", getConfigPath().toAbsolutePath().toString()};
    }

    @Override
    public String[] getJVMParameters() {

        return new String[]{
                "-Ddw.server.connector.port="+sutPort,
                "-Ddw.mediaFilesFolder="+tmpDir+"/media-files",
                "-Ddw.tempFolder="+tmpDir,
                "-Ddw.database.url="+dbUrl()
        };
    }

    @Override
    public String getBaseURL() {
        return "http://localhost:"+sutPort;
    }

    @Override
    public String getPathToExecutableJar() {
        return jarLocation;
    }

    @Override
    public String getLogMessageOfInitializedServer() {
        return "Server: Started";
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return timeoutSeconds;
    }

    @Override
    public void preStart() {
        try {
            //starting H2
            h2 = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "" + dbPort);
            h2.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postStart() {

        try {
            Class.forName("org.h2.Driver");
            sqlConnection = DriverManager.getConnection(dbUrl(), "sa", "");

            /*
                ensure that the database is clean
             */
            DbCleaner.clearDatabase_H2(sqlConnection);

            dbSpecification = Arrays.asList(new DbSpecification(DatabaseType.H2,sqlConnection).withInitSqlOnResourcePath(INIT_DB_SCRIPT_PATH));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        resetStateOfSUT();
    }

    @Override
    public void preStop() {

    }

    @Override
    public void postStop() {
        if (h2 != null) {
            h2.stop();
        }
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "se.devscout.";
    }

    @Override
    public void resetStateOfSUT() {

        deleteDir(new File(tmpDir));

//        DbCleaner.clearDatabase_H2(sqlConnection);
//        SqlScriptRunner.runCommands(sqlConnection, sqlCommands);
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }


    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                getBaseURL()  + "/api/swagger.json",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return Arrays.asList(
                AuthUtils.getForAuthorizationHeader("user", "ApiKey user"),
                AuthUtils.getForAuthorizationHeader("moderator", "ApiKey moderator"),
                AuthUtils.getForAuthorizationHeader("administrator", "ApiKey administrator")
        );
    }


    @Override
    public List<DbSpecification> getDbSpecifications() {
        return dbSpecification;
    }
}
