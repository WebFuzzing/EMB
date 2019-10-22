package org.devgateway.toolkit.persistence.mongo.spring;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientOptions;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Created by mpostelnicu on 6/12/17.
 */
@Configuration
@Profile("integration")
public class MongoTemplateTestConfig {

    @Autowired
    private MongoProperties properties;

    @Autowired
    private CustomConversions customConversions;

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private MongoClientOptions options;

    private String originalUri;

    @Bean(destroyMethod = "stop")
    public MongodProcess mongodProcess(MongodExecutable embeddedMongoServer) throws IOException {
        return embeddedMongoServer.start();
    }

    @Bean(destroyMethod = "stop")
    public MongodExecutable embeddedMongoServer(MongodStarter mongodStarter, IMongodConfig iMongodConfig)
            throws IOException {
        return mongodStarter.prepare(iMongodConfig);
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .cmdOptions(new MongoCmdOptionsBuilder().useNoJournal(true)
                        .build())
                .build();
    }

    @Bean
    public MongodStarter mongodStarter() {
        return MongodStarter.getDefaultInstance();
    }


    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(MongodProcess mongodProcess) throws Exception {
        Net net = mongodProcess.getConfig().net();
        properties.setHost(net.getServerAddress().getHostName());
        properties.setPort(net.getPort());
        properties.setDatabase(originalUri);
        properties.setUri(null);

        MongoTemplate template = new MongoTemplate(
                new SimpleMongoDbFactory(properties.createMongoClient(this.options, environment),
                        properties.getDatabase()));
        ((MappingMongoConverter) template.getConverter()).setCustomConversions(customConversions);
        return template;
    }

    @PostConstruct
    public void postConstruct() {
        //set uri string
        originalUri = new ConnectionString(properties.getUri()).getDatabase();
    }

    /**
     * Creates a shadow template configuration by adding "-shadow" as postfix of database name.
     * This is used to replicate the entire database structure in a shadow/temporary database location
     *
     * @return
     * @throws Exception
     */
    @Bean(name = "shadowMongoTemplate")
    public MongoTemplate shadowMongoTemplate(MongodProcess mongodProcess) throws Exception {
        Net net = mongodProcess.getConfig().net();
        properties.setHost(net.getServerAddress().getHostName());
        properties.setPort(net.getPort());
        properties.setDatabase(originalUri + MongoTemplateConfig.SHADOW_POSTFIX);
        properties.setUri(null);
        MongoTemplate template = new MongoTemplate(
                new SimpleMongoDbFactory(properties.createMongoClient(this.options, environment),
                        properties.getDatabase()));
        ((MappingMongoConverter) template.getConverter()).setCustomConversions(customConversions);
        return template;
    }
}
