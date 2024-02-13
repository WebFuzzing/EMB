package em.external.org.signal.registration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.signal.registration.rpc.RegistrationServiceGrpc;
import org.evomaster.client.java.controller.ExternalSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExternalEvoMasterController extends ExternalSutController {

	protected ManagedChannel channel;
	private RegistrationServiceGrpc.RegistrationServiceBlockingStub stub;


	public static void main(String[] args) {

		int controllerPort = 40100;
		if (args.length > 0) {
			controllerPort = Integer.parseInt(args[0]);
		}
		int sutPort = 12345;
		if (args.length > 1) {
			sutPort = Integer.parseInt(args[1]);
		}
		String jarLocation = "cs/grpc/signal-registration/target";
		if (args.length > 2) {
			jarLocation = args[2];
		}
		if(! jarLocation.endsWith(".jar")) {
			jarLocation += "/signal-registration-sut.jar";
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
		controller.setNeedsJdk17Options(true);
		InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

		starter.start();
	}

	private final int timeoutSeconds;
	private final int sutPort;
	private final String jarLocation;

	public ExternalEvoMasterController() {
		this(40100, "cs/grpc/signal-registration/target/signal-registration-sut.jar", 12345, 120, "java");
	}

	@Override
	public String getPackagePrefixesToCover() {
		return "org.signal.registration.";
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
		return new String[]{
				"-grpc.server.port="+sutPort
		};
	}

	public ExternalEvoMasterController(String jarLocation){
		this(40100, jarLocation, 12345, 120, "java");
	}

	@Override
	public String[] getJVMParameters() {
		return new String[]{
				"-Dmicronaut.environments=dev"
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
		return "Startup completed in ";
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
		return new RPCProblem(RegistrationServiceGrpc.RegistrationServiceBlockingStub.class, stub, RPCType.gRPC);
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
		stub = RegistrationServiceGrpc.newBlockingStub(channel);


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
