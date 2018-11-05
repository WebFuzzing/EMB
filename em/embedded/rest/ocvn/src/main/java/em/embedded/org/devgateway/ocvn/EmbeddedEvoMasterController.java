package em.embedded.org.devgateway.ocvn;


import com.p6spy.engine.spy.P6SpyDriver;
import org.devgateway.toolkit.web.spring.WebApplication;
import org.evomaster.clientJava.controller.EmbeddedSutController;
import org.evomaster.clientJava.controller.InstrumentedSutStarter;
import org.evomaster.clientJava.controller.db.DbCleaner;
import org.evomaster.clientJava.controller.problem.ProblemInfo;
import org.evomaster.clientJava.controller.problem.RestProblem;
import org.evomaster.clientJava.controllerApi.dto.AuthenticationDto;
import org.evomaster.clientJava.controllerApi.dto.SutInfoDto;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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


    private ConfigurableApplicationContext ctx;
    private Connection connection;
    private int mongodPort;


    public EmbeddedEvoMasterController() {
        this(0);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
        mongodPort = 27018;
    }


    @Override
    public String startSut() {


        ctx = SpringApplication.run(WebApplication.class,
                new String[]{"--server.port=0",
                        "--liquibase.enabled=false",
                        "--spring.data.mongodb.port=" + mongodPort,
                        "--spring.datasource.driver-class-name=" + P6SpyDriver.class.getName(),
                        "--spring.datasource.url=jdbc:p6spy:derby:memory:ocvn;create=true"
                });

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        JdbcTemplate jdbc = ctx.getBean(JdbcTemplate.class);

        try {
            connection = jdbc.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
        return ctx != null && ctx.isRunning();
    }

    @Override
    public void stopSut() {
        ctx.stop();
        ctx.close();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.devgateway.ocvn.";
    }

    @Override
    public void resetStateOfSUT() {
        //TODO reset Mongo

        DbCleaner.clearDatabase_Derby(connection, "ocvn");
    }


    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getDatabaseDriverName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/v2/api-docs?group=1ocDashboardsApi",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }


}
