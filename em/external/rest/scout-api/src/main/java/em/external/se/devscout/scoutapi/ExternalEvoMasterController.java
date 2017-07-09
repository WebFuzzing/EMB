package em.external.se.devscout.scoutapi;

import org.evomaster.clientJava.controller.ExternalSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controller.db.DbCleaner;
import org.evomaster.clientJava.controller.db.SqlScriptRunner;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;
import org.evomaster.clientJava.controllerApi.dto.HeaderDto;
import org.h2.tools.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        jarLocation += "/scout-api.jar";


        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation, sutPort);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private final int sutPort;
    private final int dbPort;
    private final String jarLocation;
    private final String tmpDir;
    private final String CONFIG_FILE = "scout_api_evomaster.yml";

    private Connection connection;
    private List<String> sqlCommands;

    public ExternalEvoMasterController(){
        this(40100, "../api/target", 12345);
    }

    public ExternalEvoMasterController(int controllerPort,
                                       String jarLocation,
                                       int sutPort) {
        this.sutPort = sutPort;
        this.dbPort = sutPort + 1;
        this.jarLocation = jarLocation;
        setControllerPort(controllerPort);

        String base = Paths.get(jarLocation).getParent().toAbsolutePath().normalize().toString();
        tmpDir = base + "/temp/tmp_scout_api/temp_"+dbPort;
        createConfigurationFile();

        try(InputStream in = getClass().getResourceAsStream("/init_db.sql")) {
            sqlCommands = (new SqlScriptRunner()).readCommands(new InputStreamReader(in));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String dbUrl(boolean withP6Spy) {

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":h2:tcp://localhost:" + dbPort + "/./temp/tmp_scout_api/testdb_" + dbPort;

        return url;
    }

    /**
           Unfortunately, it seems like Dropwizard is buggy, and has
           problems with overriding params without a YML file :(
     */
    private void createConfigurationFile() {

        //save config to same folder of JAR file
        Path path = getConfigPath();

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(InputStream is = this.getClass().getResourceAsStream("/"+ CONFIG_FILE )){
            Files.copy(is, path);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Path getConfigPath(){
        return Paths.get(jarLocation)
                .getParent()
                .resolve(CONFIG_FILE)
                .toAbsolutePath()
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
                "-Ddw.database.url="+dbUrl(true)
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
        return 60;
    }

    @Override
    public void preStart() {
        try {
            //starting H2
            Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "" + dbPort).start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postStart() {

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(dbUrl(false), "sa", "");
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

    }

    @Override
    public String getPackagePrefixesToCover() {
        return "se.devscout.";
    }

    @Override
    public void resetStateOfSUT() {

        deleteDir(new File(tmpDir));

        DbCleaner.clearDatabase_H2(connection);
        SqlScriptRunner.runCommands(connection, sqlCommands);
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
    public String getUrlOfSwaggerJSON() {
        return getBaseURL() +"/api/swagger.json";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return Arrays.asList(
                new AuthenticationDto("user") {{
                    headers.add(new HeaderDto("Authorization", "ApiKey user"));
                }},
                new AuthenticationDto("moderator") {{
                    headers.add(new HeaderDto("Authorization", "ApiKey moderator"));
                }},
                new AuthenticationDto("administrator") {{
                    headers.add(new HeaderDto("Authorization", "ApiKey administrator"));
                }}
        );
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getDatabaseDriverName() {
        return "org.h2.Driver";
    }
}
