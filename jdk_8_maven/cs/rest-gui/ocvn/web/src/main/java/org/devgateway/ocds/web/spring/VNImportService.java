package org.devgateway.ocds.web.spring;

import com.google.common.io.Files;
import com.mongodb.DBObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.shadow.ShadowClassificationRepository;
import org.devgateway.ocds.persistence.mongo.repository.shadow.ShadowOrganizationRepository;
import org.devgateway.ocds.persistence.mongo.repository.shadow.ShadowReleaseRepository;
import org.devgateway.ocds.persistence.mongo.repository.shadow.ShadowVNOrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ExcelImportService;
import org.devgateway.ocds.persistence.mongo.spring.ImportResult;
import org.devgateway.ocds.persistence.mongo.spring.OcdsSchemaValidatorService;
import org.devgateway.ocvn.persistence.mongo.dao.ImportFileTypes;
import org.devgateway.ocvn.persistence.mongo.reader.BidPlansRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.CityRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.EBidAwardRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.LocationRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.OfflineAwardRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.OrgDepartmentRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.OrgGroupRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.ProcurementPlansRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.PublicInstitutionRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.SupplierRowImporter;
import org.devgateway.ocvn.persistence.mongo.reader.TenderRowImporter;
import org.devgateway.ocvn.persistence.mongo.repository.shadow.ShadowCityRepository;
import org.devgateway.ocvn.persistence.mongo.repository.shadow.ShadowContrMethodRepository;
import org.devgateway.ocvn.persistence.mongo.repository.shadow.ShadowOrgDepartmentRepository;
import org.devgateway.ocvn.persistence.mongo.repository.shadow.ShadowOrgGroupRepository;
import org.devgateway.ocvn.persistence.mongo.repository.shadow.ShadowVNLocationRepository;
import org.devgateway.toolkit.persistence.mongo.reader.XExcelFileReader;
import org.devgateway.toolkit.persistence.mongo.spring.ShadowMongoDatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author mpostelnicu Service that imports Excel sheets from given import file in
 *         Vietnam input data format
 */
@Service
@Transactional
public class VNImportService implements ExcelImportService {

    private static final int MS_IN_SECOND = 1000;

    private static final int LOG_IMPORT_EVERY = 10000;

    private static final int VALIDATION_BATCH = 5000;

    @Autowired
    private ShadowReleaseRepository shadowReleaseRepository;

    @Autowired
    private ShadowOrganizationRepository shadowOrganizationRepository;

    @Autowired
    private ShadowVNOrganizationRepository shadowVNOrganizationRepository;

    @Autowired
    private ShadowClassificationRepository shadowClassificationRepository;

    @Autowired
    private ShadowContrMethodRepository shadowContrMethodRepository;

    @Autowired
    private ShadowVNLocationRepository shadowVNLocationRepository;

    @Autowired
    private ShadowCityRepository shadowCityRepository;

    @Autowired
    private ShadowOrgDepartmentRepository shadowOrgDepartmentRepository;

    @Autowired
    private ShadowOrgGroupRepository shadowOrgGroupRepository;

    @Autowired
    private MongoTemplate shadowMongoTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ShadowMongoDatabaseConfiguration mongoTemplateConfiguration;

    @Autowired
    private OcdsSchemaValidatorService ocdsSchemaValidator;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;

    private StringBuffer msgBuffer = new StringBuffer();

    private final Logger logger = LoggerFactory.getLogger(VNImportService.class);

    public static final String LOCATIONS_FILE_NAME = "locations";
    public static final String ORGS_FILE_NAME = "orgs";
    public static final String CITY_DEPT_GRP_NAME = "cdg";
    public static final String DATABASE_FILE_NAME = "database";
    private boolean success;

    // TODO: remove these
    // URL prototypeDatabaseFile =
    // getClass().getResource("/Prototype_Database_OCDSCore.xlsx");
    // URL organizationFile =
    // getClass().getResource("/UM_PUBINSTITU_SUPPLIERS_DQA.xlsx");
    // URL locationFile = getClass().getResource("/Location_Table_SO.xlsx");

    private void importSheet(final URL fileUrl, final String sheetName, final RowImporter<?, ?, ?> importer)
            throws Exception {
        importSheet(fileUrl, sheetName, importer, MongoConstants.IMPORT_ROW_BATCH);
    }

    private BigDecimal getMaxTenderValue() {
        Aggregation agg = Aggregation.newAggregation(match(where("tender.value.amount").exists(true)),
                group().max("tender.value.amount").as("maxTenderValue"),
                project().andInclude("maxTenderValue").andExclude(Fields.UNDERSCORE_ID));

        AggregationResults<DBObject> results = shadowMongoTemplate.aggregate(agg, "release", DBObject.class);

        return results.getMappedResults().size() == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf((double) results.getMappedResults().get(0).get("maxTenderValue"));
    }

    /**
     * Delete all data without dropping indexes
     */
    private void purgeDatabase() {
        logMessage("Purging database...");

        ScriptOperations scriptOps = shadowMongoTemplate.scriptOps();
        ExecutableMongoScript echoScript = new ExecutableMongoScript("db.dropDatabase()");
        scriptOps.execute(echoScript);

        logMessage("Database purged.");

        // //recreate inline indexes
        // mongoTemplate.setApplicationContext(applicationContext);
        //

        // create indexes that affect import performance
        mongoTemplateConfiguration.createMandatoryImportIndexes();

    }

    /**
     * This is invoked if the database has been purged
     */
    private void postImportStage() {
        // post-init indexes
        mongoTemplateConfiguration.createPostImportStructures();
    }

    /**
     * Log the message to logger but also to a stringbuffer to display online if
     * needed
     *
     * @param message
     */
    public void logMessage(final String message) {
        logger.info(message);
        msgBuffer.append(message).append("\r\n");
    }


    private void importSheet(final URL fileUrl, final String sheetName, final RowImporter<?, ?, ?> importer,
                             final int importRowBatch) {
        logMessage("<b>Importing " + sheetName + " using " + importer.getClass().getSimpleName() + "</b>");

        XExcelFileReader reader = null;
        try {
            reader = new XExcelFileReader(fileUrl.getFile(), sheetName);

            List<String[]> rows;
            long startTime = System.currentTimeMillis();
            long rowNo = 0;
            rows = reader.readRows(importRowBatch);
            while (!rows.isEmpty()) {
                if (!importer.importRows(rows)) {
                    success = false;
                }
                rowNo += rows.size();
                if (rowNo % LOG_IMPORT_EVERY == 0) {
                    logMessage("Import Speed " + rowNo * MS_IN_SECOND / (System.currentTimeMillis() - startTime)
                            + " rows per second.");
                }
                rows = reader.readRows(importRowBatch);
            }

        } catch (Exception e) {
            logMessage("<font style='color:red'> CRITICAL " + e + "</font>");
            e.printStackTrace();
            success = false;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Simple method that gets all cache names and invokes {@link Cache#clear()}
     * on all
     */
    public void clearAllCaches() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
        }
    }

    /**
     * Extracts the files from the given {@link org.devgateway.ocvn.persistence.dao.VietnamImportSourceFiles}
     * object, creates a temp dir and drops them there.
     *
     * @return the path of the temp dir created, that contains the files save
     * from {@link org.devgateway.ocvn.persistence.dao.VietnamImportSourceFiles}
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String saveSourceFilesToTempDir(final byte[] prototypeDatabase, final byte[] locations,
                                            final byte[] publicInstitutionsSuppliers, final byte[] cdg)
            throws FileNotFoundException, IOException {
        File tempDir = Files.createTempDir();
        if (prototypeDatabase != null) {
            FileOutputStream prototypeDatabaseOutputStream =
                    new FileOutputStream(new File(tempDir, DATABASE_FILE_NAME));
            prototypeDatabaseOutputStream.write(prototypeDatabase);
            prototypeDatabaseOutputStream.close();
        }

        if (locations != null) {
            FileOutputStream locationsOutputStream = new FileOutputStream(new File(tempDir, LOCATIONS_FILE_NAME));
            locationsOutputStream.write(locations);
            locationsOutputStream.close();
        }

        if (publicInstitutionsSuppliers != null) {
            FileOutputStream publicInstitutionsSuppliersOutputStream =
                    new FileOutputStream(new File(tempDir, ORGS_FILE_NAME));
            publicInstitutionsSuppliersOutputStream.write(publicInstitutionsSuppliers);
            publicInstitutionsSuppliersOutputStream.close();
        }

        if (cdg != null) {
            FileOutputStream cdgOutputStream =
                    new FileOutputStream(new File(tempDir, CITY_DEPT_GRP_NAME));
            cdgOutputStream.write(cdg);
            cdgOutputStream.close();
        }

        return tempDir.toURI().toURL().toString();
    }

    @Async
    public ImportResult importAllSheets(final List<String> fileTypes, final byte[] prototypeDatabase,
                                        final byte[] locations,
                                        final byte[] publicInstitutionsSuppliers, final byte[] cdg,
                                        final Boolean purgeDatabase,
                                        final Boolean validateData, final Boolean flagData)
            throws InterruptedException {

        String tempDirPath = null;

        success = true;

        clearAllCaches(); // clears caches before import starts

        try {
            newMsgBuffer();
            if (purgeDatabase) {
                purgeDatabase();
            }

            tempDirPath = saveSourceFilesToTempDir(prototypeDatabase, locations, publicInstitutionsSuppliers, cdg);

            if (fileTypes.contains(ImportFileTypes.LOCATIONS) && locations != null) {
                importSheet(new URL(tempDirPath + LOCATIONS_FILE_NAME), "Sheet1",
                        new LocationRowImporter(shadowVNLocationRepository, this, 1));
            }


            if (cdg != null && fileTypes.contains(ImportFileTypes.CITIES)) {
                importSheet(new URL(tempDirPath + CITY_DEPT_GRP_NAME), "City",
                        new CityRowImporter(shadowCityRepository, this, 1));
            }


            if (cdg != null && fileTypes.contains(ImportFileTypes.ORG_DEPARTMENTS)) {
                importSheet(new URL(tempDirPath + CITY_DEPT_GRP_NAME), "Department",
                        new OrgDepartmentRowImporter(shadowOrgDepartmentRepository, this, 1));
            }


            if (cdg != null && fileTypes.contains(ImportFileTypes.ORG_GROUPS)) {
                importSheet(new URL(tempDirPath + CITY_DEPT_GRP_NAME), "Group",
                        new OrgGroupRowImporter(shadowOrgGroupRepository, this, 1));
            }

            if (fileTypes.contains(ImportFileTypes.PUBLIC_INSTITUTIONS) && publicInstitutionsSuppliers != null) {
                importSheet(new URL(tempDirPath + ORGS_FILE_NAME), "UM_PUB_INSTITU_MAST",
                        new PublicInstitutionRowImporter(shadowVNOrganizationRepository, shadowCityRepository,
                                shadowOrgGroupRepository, shadowOrgDepartmentRepository,
                                this, 2));
            }

            if (fileTypes.contains(ImportFileTypes.SUPPLIERS) && publicInstitutionsSuppliers != null) {
                importSheet(new URL(tempDirPath + ORGS_FILE_NAME), "UM_SUPPLIER_ENTER_MAST",
                        new SupplierRowImporter(shadowOrganizationRepository, shadowCityRepository, this, 2));
            }

            if (prototypeDatabase != null) {
                if (fileTypes.contains(ImportFileTypes.PROCUREMENT_PLANS)) {
                    importSheet(new URL(tempDirPath + DATABASE_FILE_NAME), "ProcurementPlans",
                            new ProcurementPlansRowImporter(shadowReleaseRepository,
                                    this, shadowVNLocationRepository, 1));
                }

                if (fileTypes.contains(ImportFileTypes.BID_PLANS)) {
                    importSheet(new URL(tempDirPath + DATABASE_FILE_NAME), "BidPlans",
                            new BidPlansRowImporter(shadowReleaseRepository, this, 1));
                }

                if (fileTypes.contains(ImportFileTypes.TENDERS)) {
                    importSheet(new URL(tempDirPath + DATABASE_FILE_NAME), "Tender",
                            new TenderRowImporter(shadowReleaseRepository, this, shadowOrganizationRepository,
                                    shadowClassificationRepository, shadowContrMethodRepository,
                                    shadowVNLocationRepository, 1));
                }

                BigDecimal maxTenderValue = getMaxTenderValue();

                if (fileTypes.contains(ImportFileTypes.EBID_AWARDS)) {
                    importSheet(new URL(tempDirPath + DATABASE_FILE_NAME), "eBid_Awards", new EBidAwardRowImporter(
                            shadowReleaseRepository, this, shadowOrganizationRepository, 1, maxTenderValue));
                }

                if (fileTypes.contains(ImportFileTypes.OFFLINE_AWARDS)) {
                    importSheet(new URL(tempDirPath + DATABASE_FILE_NAME), "Offline_Awards",
                            new OfflineAwardRowImporter(shadowReleaseRepository, this, shadowOrganizationRepository, 1,
                                    maxTenderValue));
                }
            }

            if (purgeDatabase) {
                postImportStage();
            }

            if (validateData) {
                validateData();
            }

            if (flagData) {
                flagData();
            }

            if (success) {
                success = copyShadowToMain();
            } else {
                logMessage("<b>CRITICAL ERRORS FOUND DURING IMPORT. NEWLY IMPORTED DATA WILL BE DISCARDED.</b>");
            }

            logMessage("<b>IMPORT PROCESS COMPLETED.</b>");

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            clearAllCaches(); // always clears caches post import

            if (tempDirPath != null) {
                try {
                    FileUtils.deleteDirectory(Paths.get(new URL(tempDirPath).toURI()).toFile());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return new ImportResult(success, msgBuffer);
        }
    }

    @Override
    public boolean copyShadowToMain() {
        boolean r = true;
        logMessage("<b>REPLACING MAIN DATABASE WITH IMPORT RESULT</b>");
        try {
            ScriptOperations scriptOps = mongoTemplate.scriptOps();
            ExecutableMongoScript echoScript = new ExecutableMongoScript("db.dropDatabase()");
            scriptOps.execute(echoScript);

            echoScript = new ExecutableMongoScript("db.copyDatabase('ocvn-shadow','ocvn')");
            scriptOps.execute(echoScript);
            logMessage("<b>DATABASE REPLACED</b>");
        } catch (Exception e) {
            e.printStackTrace();
            r = false;
        }
        return r;
    }

    private void flagData() {

        releaseFlaggingService.processAndSaveFlagsForAllReleases(this::logMessage);

    }

    public void validateData() {

        logMessage("<b>RUNNING SCHEMA VALIDATION.</b>");

        int pageNumber = 0;
        int processedCount = 0;

        Page<Release> page;
        do {
            page = shadowReleaseRepository.findAll(new PageRequest(pageNumber++, VALIDATION_BATCH));
            page.getContent().parallelStream().map(rel -> ocdsSchemaValidator.validate(rel))
                    .filter(r -> !r.getReport().isSuccess()).forEach(r -> {
                logMessage(
                        "<font style=''>OCDS Validation Failed: " + r.toString() + "</font>");
                //success = false; //we consider validation as non critical, so we don't stop
            });
            processedCount += page.getNumberOfElements();
            logMessage("Validated " + processedCount + " releases");
        } while (!page.isLast());

        logMessage("<b>SCHEMA VALIDATION COMPLETE.</b>");
    }

    public StringBuffer getMsgBuffer() {
        return msgBuffer;
    }

    public void newMsgBuffer() {
        msgBuffer = new StringBuffer();
    }

    public OcdsSchemaValidatorService getValidationService() {
        return ocdsSchemaValidator;
    }

}
