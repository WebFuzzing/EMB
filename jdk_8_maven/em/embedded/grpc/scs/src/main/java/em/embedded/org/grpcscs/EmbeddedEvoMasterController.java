package em.embedded.org.grpcscs;

import io.grpc.*;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.grpc.scs.ScsServiceImplBaseImpl;
import org.grpc.scs.generated.ScsServiceGrpc;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmbeddedEvoMasterController extends EmbeddedSutController {

    public static void main(String[] args) {

        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        registeredService = new ScsServiceImplBaseImpl();
        setControllerPort(port);
    }



    protected ManagedChannel channel;
    private Server server;

    private ScsServiceGrpc.ScsServiceBlockingStub stub;

    private final BindableService registeredService;

    @Override
    public boolean isSutRunning() {
        return server != null && !server.isShutdown() && !server.isTerminated();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.grpc.scs.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
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
    public String startSut() {

        try {
            server = ServerBuilder.forPort(0).addService(registeredService).build();
            server.start();

            startClient();
            return "http://localhost:"+server.getPort();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String startClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", getSutPort()).usePlaintext().build();
        stub = ScsServiceGrpc.newBlockingStub(channel);


        return "started:"+!(channel.isShutdown() || channel.isTerminated());
    }

    protected int getSutPort() {
        return server.getPort();
    }

    @Override
    public void stopSut() {

        try {
            if (channel != null)
                channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
            if (server != null)
                server.shutdown().awaitTermination(2, TimeUnit.SECONDS);

            server = null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void resetStateOfSUT() {

    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }
}
