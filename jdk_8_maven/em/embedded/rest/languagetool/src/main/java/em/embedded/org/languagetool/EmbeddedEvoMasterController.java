package em.embedded.org.languagetool;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.internal.SutController;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.languagetool.server.DatabaseAccess;
import org.languagetool.server.HTTPServer;
import org.languagetool.server.HTTPServerConfig;
import org.languagetool.server.ServerTools;

import java.sql.Connection;
import java.util.List;

import static org.languagetool.server.HTTPServerConfig.DEFAULT_HOST;
import static org.languagetool.server.Server.DEFAULT_ALLOWED_IPS;
import static org.languagetool.server.Server.checkForNonRootUser;

public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private HTTPServer server;

    public static void main(String[] args){

        SutController controller = new EmbeddedEvoMasterController();
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    @Override
    public boolean isSutRunning() {
        return server != null && server.isRunning();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.languagetool";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }


    @Override
    public ProblemInfo getProblemInfo() {
        int port = server.getBoundPort();
        return new RestProblem("http://localhost:"+port+"/v2/swagger", null);
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public String startSut() {
        HTTPServerConfig config = new HTTPServerConfig(0);
        //TODO looks like there is a DB, but only for logs???
        DatabaseAccess.init(config);

        try {
            checkForNonRootUser();
            ServerTools.print("WARNING: running in HTTP mode, consider running LanguageTool behind a reverse proxy that takes care of encryption (HTTPS)");
            if (config.isPublicAccess()) {
                ServerTools.print("WARNING: running in public mode, LanguageTool API can be accessed without restrictions!");
                server = new HTTPServer(config, false, null, null);
            } else {
                server = new HTTPServer(config, false, DEFAULT_HOST, DEFAULT_ALLOWED_IPS);
            }
            server.run();
        } catch (Exception e) {
            throw new RuntimeException("Could not start LanguageTool HTTP server on " + DEFAULT_HOST + ", port " + config.getPort(), e);
        }

        return "http://localhost:" + server.getBoundPort();
    }

    @Override
    public void stopSut() {
        server.stop();
    }

    @Override
    public void resetStateOfSUT() {
        //TODO should clean thread buffer
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }
}
