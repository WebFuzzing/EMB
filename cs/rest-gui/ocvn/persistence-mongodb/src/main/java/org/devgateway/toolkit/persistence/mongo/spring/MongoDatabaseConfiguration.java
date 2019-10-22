package org.devgateway.toolkit.persistence.mongo.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by mpostelnicu on 6/12/17.
 */
@Configuration
@EnableMongoRepositories(
        basePackages = {"org.devgateway.ocds.persistence.mongo.repository.main",
                "org.devgateway.ocvn.persistence.mongo.repository.main"},
        mongoTemplateRef = "mongoTemplate"
)
public class MongoDatabaseConfiguration extends AbstractMongoDatabaseConfiguration {

    protected final Logger logger = LoggerFactory.getLogger(MongoDatabaseConfiguration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected MongoTemplate getTemplate() {
        return mongoTemplate;
    }
}
