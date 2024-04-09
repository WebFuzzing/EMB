package em.embedded.familie.ba.sak;

import com.nimbusds.jose.JOSEObjectType;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.OAuth2Config;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.mock.oauth2.token.RequestMapping;
import no.nav.security.mock.oauth2.token.RequestMappingTokenCallback;
import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.api.dto.auth.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.auth.HttpVerb;
import org.evomaster.client.java.controller.api.dto.auth.LoginEndpointDto;
import org.evomaster.client.java.controller.api.dto.auth.TokenHandlingDto;
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.sql.DbSpecification;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class EmbeddedEvoMasterController extends EmbeddedSutController {

    private static final String POSTGRES_VERSION = "13.13";

    private static final String POSTGRES_PASSWORD = "password";

    private static final int POSTGRES_PORT = 5432;

    private static final GenericContainer postgresContainer = new GenericContainer("postgres:" + POSTGRES_VERSION)
            .withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD)
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust") //to allow all connections without a password
            .withEnv("POSTGRES_DB", "familiebasak")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"))
            .withExposedPorts(POSTGRES_PORT);

    private ConfigurableApplicationContext ctx;

    private MockOAuth2Server oAuth2Server;

    private final String ISSUER_ID = "azuread";

    private final String DEFAULT_AUDIENCE = "some-audience";

    private final String PROSESSERING_ROLLE = "928636f4-fd0d-4149-978e-a6fb68bb19de";

    private final String TOKEN_PARAM = "name";

    private static final String A0 = "TaskRunner";
    private static final String A1 = "Veileder";
    private static final String A2 = "Saksbehandler";
    private static final String A3 = "Beslutter";
    private static final String A4 = "Forvalter";
    private static final String A5 = "Kode6";
    private static final String A6 = "Kode7";


    private Connection sqlConnection;
    private List<DbSpecification> dbSpecification;

    public EmbeddedEvoMasterController() {
        this(40100);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }


    public static void main(String[] args) {
        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }

    @Override
    public boolean isSutRunning() {
        return ctx!=null && ctx.isRunning();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "no.nav.familie.ba.sak.";
    }


    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {

        String url = oAuth2Server.baseUrl() + ISSUER_ID + "/token";

        return Arrays.asList(
                getAuthenticationDto(A0,url),
                getAuthenticationDto(A1,url),
                getAuthenticationDto(A2,url),
                getAuthenticationDto(A3,url),
                getAuthenticationDto(A4,url),
                getAuthenticationDto(A5,url),
                getAuthenticationDto(A6,url)
        );
    }

    private RequestMappingTokenCallback getTokenCallback(String label, List<String> groups, String id, String name) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("groups",groups);
        claims.put("name",name);
        claims.put("NAVident", id);

        Set<RequestMapping> mappings = new HashSet<>();
        RequestMapping rm = new RequestMapping(TOKEN_PARAM,label,claims,JOSEObjectType.JWT.getType());
        mappings.add(rm);

        RequestMappingTokenCallback callback = new RequestMappingTokenCallback(
                ISSUER_ID,
                mappings,
                360000
        );

        return callback;
    }

    private OAuth2Config getOAuth2Config(){

        Set<RequestMappingTokenCallback> callbacks = Set.of(
                getTokenCallback(A0, Arrays.asList(PROSESSERING_ROLLE),"Z0042", "Task Runner"),
                getTokenCallback(A1, Arrays.asList("VEILEDER"),"Z0000", "Mock McMockface"),
                getTokenCallback(A2, Arrays.asList("SAKSBEHANDLER"),"Z0001", "Foo Bar"),
                getTokenCallback(A3, Arrays.asList("BESLUTTER"),"Z0002", "John Smith"),
                getTokenCallback(A4, Arrays.asList("FORVALTER"),"Z0003", "Mario Rossi"),
                getTokenCallback(A5, Arrays.asList("KODE6"),"Z0004", "Kode Six"),
                getTokenCallback(A6, Arrays.asList("KODE7"),"Z0005", "Kode Seven")
        );

        OAuth2Config config = new OAuth2Config(
                false,
                null,
                null,
                new no.nav.security.mock.oauth2.token.OAuth2TokenProvider(),
                callbacks
                );

        return config;
    }

    private AuthenticationDto getAuthenticationDto(String label, String oauth2Url){

        AuthenticationDto dto = new AuthenticationDto(label);
        LoginEndpointDto x = new LoginEndpointDto();
        dto.loginEndpointAuth = x;

        x.externalEndpointURL = oauth2Url;
        x.payloadRaw = "name="+label+"&grant_type=authorization_code&code=foo&client_id=foo";
        x.verb = HttpVerb.POST;
        x.contentType = "application/x-www-form-urlencoded";
        x.expectCookies = false;

        TokenHandlingDto token = new TokenHandlingDto();
        token.headerPrefix = "Bearer ";
        token.httpHeaderName = "Authorization";
        token.extractFromField = "/access_token";
        x.token = token;

        return dto;
    }




//    @Override
//    public List<AuthenticationDto> getInfoForAuthentication() {
//
//        //see RolletilgangTest
//        String token_task = getToken(Arrays.asList(PROSESSERING_ROLLE),"Z0042", "Task Runner");
//        String token_veileder = getToken(Arrays.asList("VEILEDER"),"Z0000", "Mock McMockface");
//        String token_saksbehandler = getToken(Arrays.asList("SAKSBEHANDLER"),"Z0001", "Foo Bar");
//        String token_beslutter = getToken(Arrays.asList("BESLUTTER"),"Z0002", "John Smith");
//        String token_forvalter = getToken(Arrays.asList("FORVALTER"),"Z0003", "Mario Rossi");
//        String token_kode6 = getToken(Arrays.asList("KODE6"),"Z0004", "Kode Six");
//        String token_kode7 = getToken(Arrays.asList("KODE7"),"Z0005", "Kode Seven");
//
//        return Arrays.asList(
//                AuthUtils.getForAuthorizationHeader("TaskRunner", "Bearer " + token_task),
//                AuthUtils.getForAuthorizationHeader("Veileder", "Bearer " + token_veileder),
//                AuthUtils.getForAuthorizationHeader("Saksbehandler", "Bearer " + token_saksbehandler),
//                AuthUtils.getForAuthorizationHeader("Beslutter", "Bearer " + token_beslutter),
//                AuthUtils.getForAuthorizationHeader("Forvalter", "Bearer " + token_forvalter),
//                AuthUtils.getForAuthorizationHeader("Kode6", "Bearer " + token_kode6),
//                AuthUtils.getForAuthorizationHeader("Kode7", "Bearer " + token_kode7)
//        );
//    }
//
//    private String getToken(List<String> groups, String id, String name) {
//        Map<String,Object> claims = new HashMap<>();
//        claims.put("groups",groups);
//        claims.put("name",name);
//        claims.put("NAVident", id);
//
//        String token = oAuth2Server.issueToken(
//                ISSUER_ID,
//                id,
//                new DefaultOAuth2TokenCallback(
//                        ISSUER_ID,
//                        "subject",
//                        JOSEObjectType.JWT.getType(),
//                        Arrays.asList(DEFAULT_AUDIENCE),
//                        claims,
//                        360000
//                        )
//                ).serialize();
//        return token;
//    }


    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/v3/api-docs",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_5;
    }

    @Override
    public String startSut() {
        postgresContainer.start();

        oAuth2Server = new  MockOAuth2Server(getOAuth2Config());
        oAuth2Server.start();

        String wellKnownUrl = oAuth2Server.wellKnownUrl(ISSUER_ID).toString();


        String postgresURL = "jdbc:postgresql://" + postgresContainer.getHost() + ":" + postgresContainer.getMappedPort(POSTGRES_PORT) + "/familiebasak";

        //TODO should go through all the environment variables in application properties
        System.setProperty("AZUREAD_TOKEN_ENDPOINT_URL","http://fake-azure-token-endpoint.no:8080");
        System.setProperty("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT","bar");
        System.setProperty("AZURE_APP_CLIENT_ID","bar");
        System.setProperty("NAIS_APP_NAME","bar");
        System.setProperty("UNLEASH_SERVER_API_URL","http://fake-unleash-server-api.no:8080");
        System.setProperty("UNLEASH_SERVER_API_TOKEN","bar");
        System.setProperty("BA_SAK_CLIENT_ID", DEFAULT_AUDIENCE);

        ctx = SpringApplication.run(no.nav.familie.ba.sak.FamilieBaSakApplication.class, new String[]{
                "--server.port=0",
                "--spring.profiles.active=dev",
                "--management.server.port=-1",
                "--server.ssl.enabled=false",
                "--spring.datasource.url=" + postgresURL,
                "--spring.datasource.username=postgres",
                "--spring.datasource.password=" + POSTGRES_PASSWORD,
                "--sentry.logging.enabled=false",
                "--sentry.environment=local",
                //TODO check when dealing with Kafka
                "--funksjonsbrytere.kafka.producer.enabled=false",
                "--funksjonsbrytere.enabled=false",
                "--logging.level.root=OFF",
                "--logging.config=classpath:logback-spring.xml",
                "--logging.level.org.springframework=INFO",
                "--no.nav.security.jwt.issuer.azuread.discoveryurl="+wellKnownUrl,
                "--prosessering.rolle=" + PROSESSERING_ROLLE,
                "--FAMILIE_EF_SAK_API_URL=http://fake-familie-ef-sak/api",
                "--FAMILIE_KLAGE_URL=http://fake-familie-klage",
                "--FAMILIE_BREV_API_URL=http://fake-familie-brev",
                "--FAMILIE_BA_INFOTRYGD_FEED_API_URL=http://fake-familie-ba-infotrygd-feed/api",
                "--FAMILIE_BA_INFOTRYGD_API_URL=http://fake-familie-ba-infotrygd",
                "--FAMILIE_TILBAKE_API_URL=http://fake-familie-tilbake/api",
                "--PDL_URL=http://fake-pdl-api.default",
                "--FAMILIE_INTEGRASJONER_API_URL=http://fake-familie-integrasjoner/api",
                "--FAMILIE_OPPDRAG_API_URL=http://fake-familie-oppdrag/api",
                "--SANITY_FAMILIE_API_URL=http://fake-xsrv1mh6.apicdn.sanity.io/v2021-06-07/data/query/ba-brev",
                "--ECB_API_URL=http://fake-data-api.ecb.europa.eu/service/data/EXR/"
        });

        if (sqlConnection != null) {
            try {
                sqlConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            sqlConnection = DriverManager.getConnection(postgresURL, "postgres", POSTGRES_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        dbSpecification = Arrays.asList(new DbSpecification(DatabaseType.POSTGRES, sqlConnection));

        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
    //    return ctx.getEnvironment().getProperty("server.port", Integer.class);
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }

    @Override
    public void stopSut() {
        postgresContainer.stop();
        if(oAuth2Server!=null) oAuth2Server.shutdown();
        if(ctx!=null)ctx.stop();
    }

    @Override
    public void resetStateOfSUT() {
    }

    @Override
    public List<DbSpecification> getDbSpecifications() {
        return dbSpecification;
    }
}
