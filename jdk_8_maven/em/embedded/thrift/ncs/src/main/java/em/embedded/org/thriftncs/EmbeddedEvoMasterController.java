package em.embedded.org.thriftncs;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCType;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RPCProblem;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.thrift.ncs.NcsApplication;
import org.thrift.ncs.client.NcsService;

import java.sql.Connection;
import java.util.HashMap;
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
    private NcsService.Client client;
    private TTransport transport;
    private TProtocol protocol;

    @Override
    public boolean isSutRunning() {
        return ctx != null && ctx.isRunning();
    }


    @Override
    public String getPackagePrefixesToCover() {
        return "org.thrift.ncs.service.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }


    @Override
    public ProblemInfo getProblemInfo() {

        return new RPCProblem(new HashMap<String, Object>() {{
            put(NcsService.Iface.class.getName(), client);
        }});
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public String startSut() {

        ctx = SpringApplication.run(NcsApplication.class, new String[]{
                "--server.port=0"
        });

        String url = "http://localhost:"+getSutPort()+"/ncs";

        try {
            // init client
            transport = new THttpClient(url);
            protocol = new TBinaryProtocol(transport);
            client = new NcsService.Client(protocol);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        // it is not necessary here for RPC
        return url;
    }

    protected int getSutPort() {
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }

    @Override
    public void stopSut() {
        transport.close();
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
