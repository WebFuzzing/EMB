package org.devgateway.toolkit.persistence.mongo.spring;

import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

public abstract class AbstractMongoDatabaseConfiguration {

    protected abstract Logger getLogger();

    protected abstract MongoTemplate getTemplate();

    public void createMandatoryImportIndexes() {
        // vietnam specific indexes
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("planning.budget.projectID", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("planning.bidNo", Direction.ASC));
        getTemplate().indexOps(Organization.class).ensureIndex(new Index().on("identifier._id", Direction.ASC));
        getTemplate().indexOps(Organization.class)
                .ensureIndex(new Index().on("additionalIdentifiers._id", Direction.ASC));
        getTemplate().indexOps(Organization.class).ensureIndex(
                new Index().on("roles", Direction.ASC));
        getTemplate().indexOps(Organization.class).ensureIndex(new Index().on("name", Direction.ASC).unique());
        getTemplate().indexOps(VNLocation.class).ensureIndex(new Index().on("description", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.contrMethod.details", Direction.ASC));

        getLogger().info("Added mandatory Mongo indexes");
    }

    public void createCorruptionFlagsIndexes() {
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("flags.totalFlagged", Direction.ASC));

        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("flags.flaggedStats.type", Direction.ASC)
                .on("flags.flaggedStats.count", Direction.ASC)
        );

        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("flags.eligibleStats.type", Direction.ASC)
                .on("flags.eligibleStats.count", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I038_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I003_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I007_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I004_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I077_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I180_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I019_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I002_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I085_VALUE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I171_VALUE, Direction.ASC));
        getLogger().info("Added corruption flags indexes");
    }

    @PostConstruct
    public void mongoPostInit() {
        createMandatoryImportIndexes();
        createPostImportStructures();
    }

    private void createProcuringEntityIndexes() {
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.procuringEntity._id", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.procuringEntity.group._id",
                Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("tender.procuringEntity.department._id", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("tender.procuringEntity.address.postalCode", Direction.ASC));

        getTemplate().indexOps(City.class)
        .ensureIndex(new TextIndexDefinitionBuilder().onField("name").onField("id").build());

        getTemplate().indexOps(OrgDepartment.class)
        .ensureIndex(new TextIndexDefinitionBuilder().onField("name").onField("id").build());

        getTemplate().indexOps(OrgGroup.class)
        .ensureIndex(new TextIndexDefinitionBuilder().onField("name").onField("id").build());
    }

    public void createPostImportStructures() {

        createCorruptionFlagsIndexes();

        // initialize some extra indexes
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("ocid", Direction.ASC).unique());

        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.procurementMethod", Direction.ASC));
        getTemplate().indexOps(Release.class)
                .ensureIndex(new Index().on("tender.procurementMethodRationale", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.status", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("awards.status", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("awards.suppliers._id", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("awards.date", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("awards.publishedDate", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("awards.value.amount", Direction.ASC));

        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.value.amount", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.contrMethod._id", Direction.ASC));

        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.numberOfTenderers", Direction.ASC));
        getTemplate().indexOps(Release.class)
                .ensureIndex(new Index().on("tender.cancellationRationale", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.submissionMethod", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().on("tender.publicationMethod", Direction.ASC));
        getTemplate().indexOps(Release.class)
                .ensureIndex(new Index().on(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE, Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index()
                .on(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE, Direction.ASC));
        getTemplate().indexOps(Release.class)
                .ensureIndex(new Index().on("tender.items.classification._id", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("tender.items.deliveryLocation._id", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("tender.items.deliveryLocation.geometry.coordinates", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("planning.budget.projectLocation.geometry.coordinates", Direction.ASC));
        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("planning.budget.projectLocation._id", Direction.ASC));

        getTemplate().indexOps(Release.class).ensureIndex(new Index().
                on("tender.items.deliveryLocation.geometry.coordinates", Direction.ASC));

        getTemplate().indexOps(Organization.class).ensureIndex(new TextIndexDefinitionBuilder().onField("name")
                .onField("id").onField("additionalIdentifiers._id").build());

        getTemplate().indexOps(VNLocation.class)
                .ensureIndex(new TextIndexDefinitionBuilder().onField("description").onField("uri").build());

        //vietnam specific indexes:
        getTemplate().indexOps(Release.class)
                .ensureIndex(new Index().on("planning.bidPlanProjectDateApprove", Direction.ASC));

        createProcuringEntityIndexes();

        getLogger().info("Added extra Mongo indexes");

        ScriptOperations scriptOps = getTemplate().scriptOps();

        // add script to calculate the percentiles endpoint
        URL scriptFile = getClass().getResource("/tenderBidPeriodPercentilesMongo.js");
        try {
            String scriptText = IOUtils.toString(scriptFile);
            ExecutableMongoScript script = new ExecutableMongoScript(scriptText);
            scriptOps.register(new NamedMongoScript("tenderBidPeriodPercentiles", script));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add general mongo system helper methods
        URL systemScriptFile = getClass().getResource("/mongoSystemScripts.js");
        try {
            String systemScriptFileText = IOUtils.toString(systemScriptFile);
            ExecutableMongoScript script = new ExecutableMongoScript(systemScriptFileText);
            scriptOps.execute(script);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
