package em.external.io.github.proxyprint.kitchen;

import com.p6spy.engine.spy.P6SpyDriver;
import org.evomaster.clientJava.controller.ExternalSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controller.db.DbCleaner;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;
import org.evomaster.clientJava.controllerApi.dto.HeaderDto;
import org.h2.tools.Server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
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
        String jarLocation = "cs/rest/original/proxyprint/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        jarLocation += "/proxyprint.jar";

        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation, sutPort);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private final int sutPort;
    private final int dbPort;
    private final String jarLocation;
    private final String tmpDir;
    private Connection connection;


    public ExternalEvoMasterController() {
        this(40100, "cs/rest/original/proxyprint/target/proxyprint.jar", 12345);
    }

    public ExternalEvoMasterController(int controllerPort, String jarLocation, int sutPort) {
        this.sutPort = sutPort;
        this.dbPort = sutPort + 1;
        this.jarLocation = jarLocation;
        setControllerPort(controllerPort);

        String base = Paths.get(jarLocation).getParent().toAbsolutePath().normalize().toString();
        tmpDir = base + "/temp/tmp_proxyprint/temp_" + dbPort;
    }

    private String dbUrl(boolean withP6Spy) {

        String url = "jdbc";
        if (withP6Spy) {
            url += ":p6spy";
        }
        url += ":h2:tcp://localhost:" + dbPort + "/./temp/tmp_proxyprint/testdb_" + dbPort;

        return url;
    }

    @Override
    public String[] getInputParameters() {
        return new String[]{"--server.port=" + sutPort};
    }

    public String[] getJVMParameters() {
        return new String[]{
                "-Dspring.datasource.url=" + dbUrl(true) + ";DB_CLOSE_DELAY=-1",
                "-Dspring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
                "-Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "-Dspring.datasource.username=sa",
                "-Dspring.datasource.password",
                "-Dspring.jpa.show-sql=false",
                "-Dspring.jpa.hibernate.ddl-auto=create-drop",
                "-Ddocuments.path=" + tmpDir
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
        return "Started WebAppConfig in ";
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
        closeDataBaseConnection();

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(dbUrl(false), "sa", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetStateOfSUT() {
        DbCleaner.clearDatabase_H2(connection);

        deleteDir(new File("./target/temp"));

        try {
            URL url = new URL(getBaseURL() + "/admin/seed");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.connect();

            int status = con.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Invalid return code: " + status);
            }
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void preStop() {
        closeDataBaseConnection();
    }

    @Override
    public void postStop() {
        try {
            Server.shutdownTcpServer(dbUrl(false), "sa", true, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        return "io.github.proxyprint.kitchen.";
    }


    @Override
    public String getUrlOfSwaggerJSON() {
        return getBaseURL() + "/v2/api-docs";
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getDatabaseDriverName() {
        return "org.h2.Driver";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return Arrays.asList(
                new AuthenticationDto("admin") {{
                    headers.add(new HeaderDto("Authorization", encode("master", "1234")));
                }},
                new AuthenticationDto("consumer") {{
                    headers.add(new HeaderDto("Authorization", encode("joao", "1234")));
                }},
                new AuthenticationDto("manager") {{
                    headers.add(new HeaderDto("Authorization", encode("joaquim", "1234")));
                }},
                new AuthenticationDto("employee") {{
                    headers.add(new HeaderDto("Authorization", encode("mafalda", "1234")));
                }}
        );
    }

    private static String encode(String username, String password) {
        byte[] toEncode;
        try {
            toEncode = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return "Basic " + Base64.getEncoder().encodeToString(toEncode);
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
