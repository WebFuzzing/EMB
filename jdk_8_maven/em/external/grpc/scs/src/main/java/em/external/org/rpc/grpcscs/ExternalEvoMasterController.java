package em.external.org.rpc.grpcscs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.grpc.scs.generated.ScsServiceGrpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExternalEvoMasterController extends ExternalSutController {


    protected ManagedChannel channel;
    private ScsServiceGrpc.ScsServiceBlockingStub stub;


    public static void main(String[] args) {

        int controllerPort = 40100;
        if (args.length > 0) {
            controllerPort = Integer.parseInt(args[0]);
        }
        int sutPort = 12345;
        if (args.length > 1) {
            sutPort = Integer.parseInt(args[1]);
        }
        String jarLocation = "cs/rpc/grpc/artificial/grpc-scs/target";
        if (args.length > 2) {
            jarLocation = args[2];
        }
        if(! jarLocation.endsWith(".jar")) {
            jarLocation += "/rpc-grpc-scs-sut.jar";
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
        this(40100, "cs/rpc/grpc/artificial/grpc-scs/target/rpc-grpc-scs-sut.jar", 12345, 120, "java");
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.grpc.scs.";
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
        return new String[]{String.valueOf(sutPort)};
    }

    public ExternalEvoMasterController(String jarLocation){
        this(40100, jarLocation, 12345, 120, "java");
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
        return "ScsServer started";
    }

    @Override
    public long getMaxAwaitForInitializationInSeconds() {
        return timeoutSeconds;
    }

    @Override
    public void preStart() {
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RPCProblem(ScsServiceGrpc.ScsServiceBlockingStub.class, stub, RPCType.gRPC);
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public void postStart() {

        startClient();
    }

    private String startClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", sutPort).usePlaintext().build();
        stub = ScsServiceGrpc.newBlockingStub(channel);


        return "started:"+!(channel.isShutdown() || channel.isTerminated());
    }


    @Override
    public void preStop() {

        try {
            if (channel != null)
                channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void postStop() {

    }

    @Override
    public void resetStateOfSUT() {

    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }
}
