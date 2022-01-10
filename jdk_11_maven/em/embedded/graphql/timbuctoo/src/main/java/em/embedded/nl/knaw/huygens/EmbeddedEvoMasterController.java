package em.embedded.nl.knaw.huygens;


import nl.knaw.huygens.timbuctoo.server.TimbuctooV4;
import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.GraphQlProblem;
import org.evomaster.client.java.controller.problem.ProblemInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

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

    private String tmpFolder = "tmp";

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
        //System.setProperty("dw.server.connector.port", "0");

        tmpFolder = "tmpFolder";

        resetStateOfSUT();

        System.setProperty("timbuctoo_dataPath",tmpFolder);
        System.setProperty("timbuctoo_authPath",tmpFolder);
        System.setProperty("timbuctoo_port", "0");
        System.setProperty("timbuctoo_adminPort","8081");
        System.setProperty("base_uri","http://localhost:0");
        System.setProperty("timbuctoo_elasticsearch_host","localhost");
        System.setProperty("timbuctoo_elasticsearch_port","9200");
        System.setProperty("timbuctoo_elasticsearch_user","elastic");
        System.setProperty("timbuctoo_elasticsearch_password","changeme");
        System.setProperty("timbuctoo_search_url","");
        System.setProperty("timbuctoo_indexer_url", "http://localhost:3000");

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

        return "http://localhost:" + application.getJettyPort();
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
        try {
            deleteDir(new File(tmpFolder));
            Files.createDirectory(Path.of(tmpFolder));
            Files.copy(Path.of("src","main","resources","users.json"), Path.of(tmpFolder,"users.json"));
            Files.copy(Path.of("src","main","resources","logins.json"), Path.of(tmpFolder,"logins.json"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProblemInfo getProblemInfo() {

       return new GraphQlProblem("/v5/graphql");
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return List.of(AuthUtils.getForAuthorizationHeader("user", "u33707283d426f900d4d33707283d426f900d4d0d"));
    }

    public Connection getConnection() {
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
