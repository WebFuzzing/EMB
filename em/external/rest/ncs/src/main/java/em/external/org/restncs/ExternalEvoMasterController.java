package em.external.org.restncs;

import org.evomaster.clientJava.controller.ExternalSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;

import java.sql.Connection;
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
        String jarLocation = "cs/rest/artificial/ncs/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        jarLocation += "/rest-ncs.jar";

        ExternalEvoMasterController controller =
                new ExternalEvoMasterController(controllerPort, jarLocation, sutPort);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private final int sutPort;
    private final String jarLocation;

    public ExternalEvoMasterController() {
        this(40100, "cs/rest/artificial/ncs/target/rest-ncs.jar", 12345);
    }

    public ExternalEvoMasterController(int controllerPort, String jarLocation, int sutPort) {
        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        setControllerPort(controllerPort);
    }


    @Override
    public String[] getInputParameters() {
        return new String[]{"--server.port=" + sutPort};
    }

    @Override
    public String[] getJVMParameters() {
        return new String[0];
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
        return "Started NcsApplication in ";
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return 60;
    }

    @Override
    public void preStart() {
    }

    @Override
    public void postStart() {
    }

    @Override
    public void resetStateOfSUT() {
    }

    @Override
    public void preStop() {
    }

    @Override
    public void postStop() {
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.restncs.";
    }


    @Override
    public String getUrlOfSwaggerJSON() {
        return getBaseURL() + "/v2/api-docs";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getDatabaseDriverName() {
        return null;
    }
}
