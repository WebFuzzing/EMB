package em.embedded.org.javiermf.features;


import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType;
import org.evomaster.client.java.sql.DbCleaner;
import org.evomaster.client.java.sql.SqlScriptRunner;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.javiermf.features.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
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
    private Connection sqlConnection;
    private String INIT_DB_SCRIPT_PATH = "/data.sql";

    private List<DbSpecification> dbSpecification;


    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);

    }

    @Override
    public String startSut() {

        ctx = SpringApplication.run(Application.class, new String[]{
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;",
                "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "--spring.datasource.username=sa",
                "--spring.datasource.password"
        });

          if (sqlConnection != null) {
            try {
                sqlConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        JdbcTemplate jdbc = ctx.getBean(JdbcTemplate.class);

        try {
            sqlConnection = jdbc.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*
            the application will be initialized some data in database
            to consistently manage data by evomaster
         */
        DbCleaner.clearDatabase_H2(sqlConnection);

        dbSpecification = Arrays.asList(new DbSpecification(DatabaseType.H2,sqlConnection)
                .withInitSqlOnResourcePath(INIT_DB_SCRIPT_PATH));

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
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "org.javiermf.features.";
    }

    @Override
    public void resetStateOfSUT() {
//        DbCleaner.clearDatabase_H2(sqlConnection);
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/swagger.json",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }



    @Override
    public List<DbSpecification> getDbSpecifications() {
        return dbSpecification;
    }

}
