package em.embedded.org.signal.registration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.signal.registration.rpc.RegistrationServiceGrpc;
import java.util.List;

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
		setControllerPort(port);
	}

	protected ManagedChannel channel;
	private RegistrationServiceGrpc.RegistrationServiceBlockingStub stub;

	private EmbeddedServer server;
	private ApplicationContext ctx;

	@Override
	public boolean isSutRunning() {
		return ctx != null && ctx.isRunning();
	}

	@Override
	public String getPackagePrefixesToCover() {
		return "org.signal.registration.";
	}

	@Override
	public List<AuthenticationDto> getInfoForAuthentication() {
		return null;
	}

	@Override
	public ProblemInfo getProblemInfo() {
		return new RPCProblem(RegistrationServiceGrpc.RegistrationServiceBlockingStub.class, stub, RPCType.gRPC);
	}

	@Override
	public SutInfoDto.OutputFormat getPreferredOutputFormat() {
		return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
	}

	@Override
	public String startSut() {

		try {
			server = ApplicationContext.run(EmbeddedServer.class,CollectionUtils.mapOf(
					"micronaut.environments","dev",
					"grpc.server.port", "${random.port}"
			), "dev");
			ctx = server.getApplicationContext();

			startClient();
			return "http://localhost:"+server.getPort();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String startClient() {
		channel = ManagedChannelBuilder.forAddress("localhost", getSutPort()).usePlaintext().build();
		stub = RegistrationServiceGrpc.newBlockingStub(channel);


		return "started:"+!(channel.isShutdown() || channel.isTerminated());
	}

	protected int getSutPort() {
		return server.getPort();
	}

	@Override
	public void stopSut() {
		server.stop();
		ctx.stop();
	}

	@Override
	public void resetStateOfSUT() {

	}

	@Override
	public List<DbSpecification> getDbSpecifications() {
		return null;
	}
}
