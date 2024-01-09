package em.embedded.io.github.proxyprint.kitchen;

import io.github.proxyprint.kitchen.WebAppConfig;
import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType;
import org.evomaster.client.java.sql.DbCleaner;
import org.evomaster.client.java.sql.DbSpecification;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private List<DbSpecification> dbSpecification;


    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }

    @Override
    public String startSut() {

        ctx = SpringApplication.run(WebAppConfig.class, new String[]{
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MVCC=true;",
                "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "--spring.datasource.username=sa",
                "--spring.datasource.password",
                "--spring.jpa.show-sql=false",
                "--spring.jpa.hibernate.ddl-auto=create-drop",
                "--documents.path=./target/temp"
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

        dbSpecification = Arrays.asList(new DbSpecification(DatabaseType.H2,sqlConnection)
                .withDisabledSmartClean());

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
        return "io.github.proxyprint.kitchen.";
    }

    @Override
    public void resetStateOfSUT() {
        DbCleaner.clearDatabase_H2(sqlConnection);

        deleteDir(new File("./target/temp"));

        try {
            URL url = new URL("http://localhost:" + getSutPort() + "/admin/seed");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.connect();

            int status = con.getResponseCode();
            if(status != 200){
                throw new RuntimeException("Invalid return code: "+ status);
            }
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/v2/api-docs",
                //Spring Actuator endpoints
                Arrays.asList("/heapdump", "/heapdump.json",
                        "/autoconfig", "/autoconfig.json",
                        "/beans", "/beans.json",
                        "/configprops", "/configprops.json",
                        "/dump", "/dump.json",
                        "/env", "/env.json", "/env/{name}",
                        "/error",
                        "/health", "/health.json",
                        "/info", "/info.json",
                        "/mappings", "/mappings.json",
                        "/metrics", "/metrics.json", "/metrics/{name}",
                        "/trace", "/trace.json")
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }



    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return Arrays.asList(
                AuthUtils.getForBasic("admin","master","1234"),
                AuthUtils.getForBasic("consumer","joao","1234"),
                AuthUtils.getForBasic("manager","joaquim","1234"),
                AuthUtils.getForBasic("employee","mafalda","1234")
        );
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

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return dbSpecification;
    }
}
