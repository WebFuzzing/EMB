package em.embedded.textsecuregcm;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.sql.DbSpecification;
import org.whispersystems.textsecuregcm.WhisperServerService;

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

    private WhisperServerService application;

    @Override
    public boolean isSutRunning() {
        return false;
    }

    @Override
    public String getPackagePrefixesToCover() {
        return null;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return null;
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return null;
    }

    @Override
    public String startSut() {
        application = new WhisperServerService();


//        try {
//            application.run("server", "src/main/resources/em-sample.yml");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {

        }

//        while(!application.getJettyServer().isStarted()) {
//            try {
//                Thread.sleep(3_000);
//            } catch (InterruptedException e) {
//
//            }
//        }

//        return "http://localhost:" + application.getJettyPort();
        return null;

    }

    @Override
    public void stopSut() {

    }

    @Override
    public void resetStateOfSUT() {

    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }
}
