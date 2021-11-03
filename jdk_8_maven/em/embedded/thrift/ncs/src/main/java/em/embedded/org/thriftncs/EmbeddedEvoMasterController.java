package em.embedded.org.thriftncs;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.thrift.ncs.NcsApplication;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * created by manzhang on 2021/11/3
 */
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

    private ConfigurableApplicationContext ctx;

    @Override
    public boolean isSutRunning() {
        return ctx != null && ctx.isRunning();
    }


    @Override
    public String getPackagePrefixesToCover() {
        return "org.thrift.ncs";
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
    public ProblemInfo getProblemInfo() {
        return new RPCProblem(Arrays.asList("org.thrift.ncs.NcsService$Iface"));

    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public String startSut() {

        ctx = SpringApplication.run(NcsApplication.class, new String[]{
                "--server.port=0"
        });


        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }

    @Override
    public void stopSut() {
        ctx.stop();
    }

    @Override
    public void resetStateOfSUT() {

    }
}
