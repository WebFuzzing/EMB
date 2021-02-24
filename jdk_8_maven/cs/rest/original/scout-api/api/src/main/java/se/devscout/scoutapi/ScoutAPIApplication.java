package se.devscout.scoutapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import se.devscout.scoutapi.activityimporter.ActivitiesImporter;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.apikey.ApiKeyAuthenticator;
import se.devscout.scoutapi.auth.google.GoogleAuthenticator;
import se.devscout.scoutapi.dao.*;
import se.devscout.scoutapi.model.*;
import se.devscout.scoutapi.resource.*;
import se.devscout.scoutapi.resource.v1.ActivityResourceV1;
import se.devscout.scoutapi.resource.v1.CategoryResource;
import se.devscout.scoutapi.resource.v1.FavouritesResourceV1;
import se.devscout.scoutapi.textanalyzer.RelatedActivitiesUpdater;
import se.devscout.scoutapi.util.MediaFileUtils;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ScoutAPIApplication extends Application<ScoutAPIConfiguration> {

    public static final SimpleFilterProvider DEFAULT_FILTER_PROVIDER = new SimpleFilterProvider().addFilter("custom", SimpleBeanPropertyFilter.serializeAll());

    private final HibernateBundle<ScoutAPIConfiguration> hibernate = new HibernateBundle<ScoutAPIConfiguration>(
            SystemMessage.class,
            Tag.class,
            TagDerived.class,
            MediaFile.class,
            Activity.class,
            ActivityDerived.class,
            ActivityRating.class,
            ActivityProperties.class,
            ActivityPropertiesTag.class,
            ActivityPropertiesMediaFile.class,
            ActivityRelation.class,
            User.class,
            UserIdentity.class) {
        public DataSourceFactory getDataSourceFactory(ScoutAPIConfiguration scoutAPIConfiguration) {
            return scoutAPIConfiguration.getDatabase();
        }

        @Override
        public SessionFactory getSessionFactory() {
            return super.getSessionFactory();
        }
    };

    public static final String REALM = "Scout Admin Tool";

    private ScheduledExecutorService crawler;


    private Server jettyServer;

    public static void main(String[] args) throws Exception {
        new ScoutAPIApplication().run(args);
    }

    public Connection getConnection(){
        try {
            return hibernate
                    .getSessionFactory()
                    .getSessionFactoryOptions()
                    .getServiceRegistry()
                    .getService(ConnectionProvider.class)
                    .getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ScoutAPIApplication(){
    }


    public int getJettyPort(){
        return ((AbstractNetworkConnector)jettyServer.getConnectors()[0]).getLocalPort();
    }

    public Server getJettyServer() {
        return jettyServer;
    }


    @Override
    public void run(ScoutAPIConfiguration scoutAPIConfiguration, Environment environment) throws Exception {
        initTags(scoutAPIConfiguration.getDefaultTags());
        initUsers(scoutAPIConfiguration.getDefaultUsers());
        initFolder(scoutAPIConfiguration.getMediaFilesFolder());
        initFolder(scoutAPIConfiguration.getTempFolder());
        initSystemMessages(scoutAPIConfiguration.getDefaultSystemMessages());

        initResources(scoutAPIConfiguration, environment);

//        Add support for logging all requests and responses. Logging configuration must also be changed/set.
//        environment.jersey().register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true));

        environment.getObjectMapper().setFilterProvider(DEFAULT_FILTER_PROVIDER);
        environment.getObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'"));

        final ConfigurationHealthCheck healthCheck = new ConfigurationHealthCheck();
        environment.healthChecks().register("config-health-check", healthCheck);
        initAuthentication(environment, scoutAPIConfiguration);

        initTasks(scoutAPIConfiguration, environment);

        if (scoutAPIConfiguration.getAutoUpdateIntervalSeconds() > 0) {
            initActivityUpdater(scoutAPIConfiguration, environment);
        }
//        ChainedAuthFactory<AuthResult> chainedFactory = new ChainedAuthFactory<>(
//                new OAuthFactory<AuthResult>(new ApiKeyAuthenticator(userDao), REALM, AuthResult.class).prefix(ApiKeyAuthenticator.ID),
//                new OAuthFactory<AuthResult>(new GoogleAuthenticator(userDao), REALM, AuthResult.class).prefix(GoogleAuthenticator.ID)
//        );
//        environment.jersey().register(AuthFactory.binder(chainedFactory));


        environment.lifecycle().addServerLifecycleListener(server -> jettyServer = server);
    }

    private void initTasks(ScoutAPIConfiguration cfg, Environment environment) {
        //TODO: Task should only be available to administrators.
        environment.admin().addTask(new ActivitiesImporter.Task(
                getCrawlerTempFolder(cfg),
                cfg.getCrawlerUser(),
                hibernate.getSessionFactory()));

        //TODO: Task should only be available to administrators.
        environment.admin().addTask(new MediaFileUtils.CleanResizedImagesCacheTask(cfg.getMediaFilesFolder()));

        //TODO: Task should only be available to administrators.
        environment.admin().addTask(new MediaFileUtils.AutoAssignMediaFileToTags(
                hibernate.getSessionFactory(),
                cfg.getMediaFilesFolder(),
                cfg.getCrawlerUser()));
    }

    private File getCrawlerTempFolder(ScoutAPIConfiguration cfg) {
        return new File(cfg.getTempFolder(), "crawler");
    }

    private void initAuthentication(Environment environment, ScoutAPIConfiguration configuration) {
        List<AuthFilter> filters = Lists.newArrayList(
                new OAuthCredentialAuthFilter.Builder<>()
                        .setAuthenticator((Authenticator) new ApiKeyAuthenticator(
                                hibernate.getSessionFactory()))
                        .setPrefix(ApiKeyAuthenticator.ID)
                        .buildAuthFilter(),
                new OAuthCredentialAuthFilter.Builder<>()
                        .setAuthenticator((Authenticator) new GoogleAuthenticator(
                                hibernate.getSessionFactory(),
                                configuration.getGoogleAuthentication()))
                        .setPrefix(GoogleAuthenticator.ID)
                        .buildAuthFilter()
        );
        environment.jersey().register(new AuthDynamicFeature(new ChainedAuthFilter(filters)));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthResult.class));
    }

    private void initResources(ScoutAPIConfiguration scoutAPIConfiguration, Environment environment) throws IOException {
        SystemMessageDao systemMessageDao = new SystemMessageDao(hibernate.getSessionFactory());
        UserDao userDao = new UserDao(hibernate.getSessionFactory());
        TagDao tagDao = new TagDao(hibernate.getSessionFactory());
        MediaFileDao mediaFileDao = new MediaFileDao(hibernate.getSessionFactory());
        ActivityDao activityDao = new ActivityDao(hibernate.getSessionFactory());
        ActivityRatingDao activityRatingDao = new ActivityRatingDao(hibernate.getSessionFactory());

        environment.jersey().register(new UserResource(userDao));
        environment.jersey().register(new SystemMessageResource(systemMessageDao));
        environment.jersey().register(new TagResource(tagDao));
        environment.jersey().register(new CategoryResource(tagDao));
        environment.jersey().register(new MediaFileResource(mediaFileDao, scoutAPIConfiguration.getMediaFilesFolder()));
        environment.jersey().register(new ActivityResourceV2(activityDao, activityRatingDao));
        environment.jersey().register(new ActivityResourceV1(activityDao, activityRatingDao));
        environment.jersey().register(new SystemResource());
        environment.jersey().register(new FavouritesResourceV1(activityRatingDao, activityDao));

        environment.jersey().register(MultiPartFeature.class);

        environment.jersey().register(new ApiListingResource());
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        BeanConfig config = new BeanConfig();
        config.setTitle("API for Aktivitetsbanken");
        config.setVersion("1.0 and 2.0");
        config.setContact("/dev/scout");
        config.setBasePath("api");
        config.setDescription("An open API for searching activities suitable for boy scouts and girl guides. The data is mirrored from www.aktivitetsbanken.se.");
        config.setResourcePackage(Joiner.on(',').join("se.devscout.scoutapi.resource", "se.devscout.scoutapi.resource.v1"));
        config.setScan(true);
    }

    private void initActivityUpdater(final ScoutAPIConfiguration cfg, Environment environment) throws IOException, TransformerException, ParserConfigurationException, JAXBException {
        crawler = environment.lifecycle().scheduledExecutorService("crawler").build();
        crawler.scheduleAtFixedRate(
                () -> {
                    long abortTime = System.currentTimeMillis() + ((int) (0.9 * cfg.getAutoUpdateIntervalSeconds()) * 1000);

                    new ActivitiesImporter(
                            getCrawlerTempFolder(cfg),
                            cfg.getCrawlerUser(),
                            hibernate.getSessionFactory(),
                            abortTime)
                            .run();

                    new RelatedActivitiesUpdater(
                            cfg.getSimilarityCalculatorConfiguration(),
                            hibernate.getSessionFactory(),
                            abortTime)
                            .run();
                },
                10L,
                cfg.getAutoUpdateIntervalSeconds(),
                TimeUnit.SECONDS);
    }

    private void initFolder(File folder) {
        if (folder.exists()) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException(folder.getAbsolutePath() + " exists and is not a folder");
            }
        } else {
            if (!folder.mkdirs()) {
                throw new IllegalArgumentException("Could not create " + folder.getAbsolutePath());
            }
        }
    }

    private void initTags(List<Tag> tags) {
        Session session = hibernate.getSessionFactory().openSession();
        ManagedSessionContext.bind(session);
        Transaction transaction = session.beginTransaction();

        TagDao tagDao = new TagDao(hibernate.getSessionFactory());
        tags.stream().forEach(tag -> {
            Tag existingTag = tagDao.read(tag.getGroup(), tag.getName());
            if (existingTag == null) {
                tagDao.create(tag);
            }
        });

        transaction.commit();
        session.close();
    }

    private void initSystemMessages(List<SystemMessage> msgs) {
        Session session = hibernate.getSessionFactory().openSession();
        ManagedSessionContext.bind(session);
        Transaction transaction = session.beginTransaction();

        SystemMessageDao dao = new SystemMessageDao(hibernate.getSessionFactory());
        List<String> existingMessages = dao.all().stream().map(sm -> sm.getKey()).collect(Collectors.toList());
        msgs.stream().forEach(msg -> {
            if (!existingMessages.contains(msg.getKey())) {
                dao.create(msg);
            }
        });

        transaction.commit();
        session.close();
    }

    private void initUsers(List<User> users) {
        Session session = hibernate.getSessionFactory().openSession();
        ManagedSessionContext.bind(session);
        Transaction transaction = session.beginTransaction();

        UserDao userDao = new UserDao(hibernate.getSessionFactory());
        users.stream().forEach(user -> {
            List<User> existingUsers = userDao.byName(user.getName());
            if (existingUsers.isEmpty()) {

                // We need to re-add each identity since the identities read from the configuration lack back-reference to the user itself (required when saving using JPA).
                ArrayList<UserIdentity> userIdentities = new ArrayList<UserIdentity>(user.getIdentities());
                user.getIdentities().clear();
                userIdentities.stream().forEach(userIdentity1 -> user.addIdentity(userIdentity1.getType(), userIdentity1.getValue()));

                // Save
                userDao.create(user);
            }
        });

        transaction.commit();
        session.close();
    }

    @Override
    public String getName() {
        return "scout-api";
    }

    @Override
    public void initialize(Bootstrap<ScoutAPIConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);

//        AttributeTypeDao attributeTypeDao = new AttributeTypeDao(hibernate.getSessionFactory());
//        attributeTypeDao.initDefaultTypes(adminToolConfiguration.getDefaultAttributes());

        bootstrap.addBundle(new MigrationsBundle<ScoutAPIConfiguration>() {
            public DataSourceFactory getDataSourceFactory(ScoutAPIConfiguration scoutAPIConfiguration) {
                return scoutAPIConfiguration.getDatabase();
            }
        });

        bootstrap.addBundle(new ViewBundle<ScoutAPIConfiguration>());

        bootstrap.addBundle(new AssetsBundle("/assets", "/static", "assets/index.html"));


        //NOTE: comment out to run manual tests
//        bootstrap.setConfigurationSourceProvider(
//                new ResourceConfigurationSourceProvider());


        super.initialize(bootstrap);
    }

}
