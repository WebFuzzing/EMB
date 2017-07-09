package em.embedded.se.devscout.scoutapi;

import org.evomaster.clientJava.controller.EmbeddedSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controller.db.DbCleaner;
import org.evomaster.clientJava.controller.db.SqlScriptRunner;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;
import org.evomaster.clientJava.controllerApi.dto.HeaderDto;
import se.devscout.scoutapi.ScoutAPIApplication;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to start/stop the SUT. This will be controller by the EvoMaster process
 */
public class EmbeddedEvoMasterController extends EmbeddedSutController {

    public static void main(String[] args){

        int port = 40100;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private ScoutAPIApplication application;
    private Connection connection;
    private List<String> sqlCommands;

    public EmbeddedEvoMasterController(){
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);

        try(InputStream in = getClass().getResourceAsStream("/init_db.sql")) {
            sqlCommands = (new SqlScriptRunner()).readCommands(new InputStreamReader(in));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String startSut() {
        application = new ScoutAPIApplication();

        //Dirty hack for DW...
        System.setProperty("dw.server.connector.port", "0");

        try {
            application.run("server", "src/main/resources/scout_api_evomaster.yml");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
        }

        while (!application.getJettyServer().isStarted()) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
            }
        }

        connection = application.getConnection();

        resetStateOfSUT();

        return "http://localhost:" + application.getJettyPort();
    }


    @Override
    public boolean isSutRunning() {
        if (application == null) {
            return false;
        }

        return application.getJettyServer().isRunning();
    }

    @Override
    public void stopSut() {
        if (application != null) {
            try {
                application.getJettyServer().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "se.devscout.";
    }

    @Override
    public void resetStateOfSUT() {

        deleteDir(new File("./target/temp"));

        DbCleaner.clearDatabase_H2(connection);
        SqlScriptRunner.runCommands(connection, sqlCommands);
    }

    @Override
    public String getUrlOfSwaggerJSON() {
        return "http://localhost:" + application.getJettyPort() + "/api/swagger.json";
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


    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
