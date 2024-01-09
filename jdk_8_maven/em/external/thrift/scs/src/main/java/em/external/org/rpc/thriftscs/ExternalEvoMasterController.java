package em.external.org.rpc.thriftscs;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.thrift.scs.client.ScsService;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class ExternalEvoMasterController extends ExternalSutController {

    private ScsService.Client client;
    private TTransport transport;
    private TProtocol protocol;

    public static void main(String[] args) {

        int controllerPort = 40100;
        if (args.length > 0) {
            controllerPort = Integer.parseInt(args[0]);
        }
        int sutPort = 12345;
        if (args.length > 1) {
            sutPort = Integer.parseInt(args[1]);
        }
        String jarLocation = "cs/rpc/thrift/artificial/thrift-scs/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/rpc-thrift-scs-sut.jar";
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
    private final String jarLocation;

    public ExternalEvoMasterController() {
        this(40100, "cs/rpc/thrift/artificial/thrift-scs/target/rpc-thrift-scs-sut.jar", 12345, 120, "java");
    }

    public ExternalEvoMasterController(String jarLocation){
        this(40100, jarLocation, 12345, 120, "java");
    }

    public ExternalEvoMasterController(int controllerPort, String jarLocation, int sutPort, int timeoutSeconds, String command) {
        this.sutPort = sutPort;
        this.jarLocation = jarLocation;
        this.timeoutSeconds = timeoutSeconds;
        setControllerPort(controllerPort);
        setJavaCommand(command);
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
        return "Started ScsApplication in ";
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return timeoutSeconds;
    }

    @Override
    public void preStart() {
    }

    @Override
    public void postStart() {
        String url = "http://localhost:"+sutPort+"/scs";
        try {
            // init client
            transport = new THttpClient(url);
            protocol = new TBinaryProtocol(transport);
            client = new ScsService.Client(protocol);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void resetStateOfSUT() {
    }

    @Override
    public void preStop() {
        transport.close();
    }

    @Override
    public void postStop() {
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.thrift.scs.service.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }


    @Override
    public ProblemInfo getProblemInfo() {

        return new RPCProblem(new HashMap<String, Object>() {{
            put(ScsService.Iface.class.getName(), client);
        }});
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }

}

