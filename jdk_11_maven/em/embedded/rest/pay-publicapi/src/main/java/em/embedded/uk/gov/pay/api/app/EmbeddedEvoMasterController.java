package em.embedded.uk.gov.pay.api.app;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.sql.DbSpecification;
import org.testcontainers.containers.GenericContainer;
import uk.gov.pay.api.app.PublicApi;

import java.io.File;
import java.util.List;

public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private static final int REDIS_PORT = 6379;

    private static final String REDIS_VERSION = "7.2.3";

    private static final GenericContainer redisContainer = new GenericContainer("redis:" + REDIS_VERSION)
            .withExposedPorts(REDIS_PORT);

    public static void main(String[] args) {
        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    private PublicApi application;

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }

    @Override
    public boolean isSutRunning() {
        if (application == null) {
            return false;
        }

        return application.getJettyServer().isRunning();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "uk.gov.pay.api.app.";
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + application.getJettyPort() + "/api/swagger.json",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public String startSut() {

        redisContainer.start();

//        REDIS_URL
        application = new PublicApi();

        //Dirty hack for DW...
        System.setProperty("dw.server.connector.port", "0");
        System.setProperty("REDIS_URL", "localhost:" + REDIS_PORT);

        try {
            application.run("server", "/src/main/resources/em_config.yaml");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {

        }
        while (!application.getJettyServer().isStarted()) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
            }
        }

        int p = application.getJettyPort();
        return "http://localhost:" + application.getJettyPort();
    }

    @Override
    public void stopSut() {
        if (application != null) {
            try {
                application.getJettyServer().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redisContainer.stop();
    }

    @Override
    public void resetStateOfSUT() {
        deleteDir(new File("./target/temp"));
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return null;
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
