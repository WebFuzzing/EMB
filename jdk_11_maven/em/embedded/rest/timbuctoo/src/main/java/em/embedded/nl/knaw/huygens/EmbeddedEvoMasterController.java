package em.embedded.nl.knaw.huygens;


import app.coronawarn.verification.VerificationApplication;
import nl.knaw.huygens.timbuctoo.server.TimbuctooV4;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.db.DbCleaner;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Class used to start/stop the SUT. This will be controller by the EvoMaster process
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


    private TimbuctooV4 application;

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }

    @Override
    public String startSut() {

        application = new TimbuctooV4();

        //Dirty hack for DW...
        System.setProperty("dw.server.connector.port", "0");

        try {
            application.run("server", "src/main/resources/timbuctoo_evomaster.yml");
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

        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }


    @Override
    public boolean isSutRunning() {
        if (application == null) {
            return false;
        }

        return application.getJettyServer().isRunning();
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
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "nl.knaw.huygens.";
    }

    @Override
    public void resetStateOfSUT() {

    }

    @Override
    public ProblemInfo getProblemInfo() {

        String schema = new Scanner(EmbeddedEvoMasterController.class.getResourceAsStream("/api-docs.json"), "UTF-8").useDelimiter("\\A").next();

        return new RestProblem(
                null, //"http://localhost:" + getSutPort() + "/api/docs",
                null,
                schema
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    public Connection getConnection() {
        return connection;
    }



}
