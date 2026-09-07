package it.unipi.LoveMining.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.*; // Import for conversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() { return "LoveMining"; }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String uri = "mongodb://10.1.1.14:27017,10.1.1.15:27017,10.1.1.16:27017/?replicaSet=lsmdb";

        // Write and Read preferences
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                .writeConcern(WriteConcern.W1.withJournal(false)) // w:1 and j:false
                .readPreference(ReadPreference.nearest()) // nearest
                .build();

        return MongoClients.create(settings);
    }

    // This method overrides the default converter and removes the _class field from the documents saved in Mongo
    @Override
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory, MongoCustomConversions customConversions,  MongoMappingContext mappingContext) {

        // 1. Create the standard resolver
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);

        // 2. Create the converter
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);

        // 3. Set custom conversions (important to prevent breaking other features)
        converter.setCustomConversions(customConversions);

        // 4. THE SOLUTION: Set the TypeMapper to null to remove the _class field
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return converter;
    }
}