package se.devscout.scoutapi.textanalyzer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import se.devscout.scoutapi.AbstractBatchJob;
import se.devscout.scoutapi.dao.ActivityDao;
import se.devscout.scoutapi.dao.DataAccessUtils;
import se.devscout.scoutapi.dao.UserDao;
import se.devscout.scoutapi.model.User;
import se.devscout.scoutapi.textanalyzer.comparator.*;
import se.devscout.scoutapi.textanalyzer.report.Report;
import se.devscout.scoutapi.textanalyzer.simplify.Simplifier;
import se.devscout.scoutapi.textanalyzer.simplify.SimplifyRule;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RelatedActivitiesUpdater extends AbstractBatchJob {

    private static final NumberFormat NUMBER_FORMAT;
    private static final double FREE_MEMORY_LIMIT = 0.3;
    private static final int NUMBER_OF_REPORT_FILES_PER_RUN = 3;

    static {
        NUMBER_FORMAT = NumberFormat.getNumberInstance();
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setMinimumFractionDigits(2);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedActivitiesUpdater.class);
    private Configuration configuration;
    private SessionFactory sessionFactory;
    private long abortTime;

    public RelatedActivitiesUpdater(Configuration configuration, SessionFactory sessionFactory, long abortTime) {
        this.configuration = configuration;
        this.sessionFactory = sessionFactory;
        this.abortTime = abortTime;
    }

    private static class ActivityMetadataComparatorWeight {
        private ActivityMetadataComparator comparator;
        private double weight;

        public ActivityMetadataComparatorWeight(ActivityMetadataComparator comparator, double weight) {
            this.comparator = comparator;
            this.weight = weight;
        }
    }

    @Override
    public void run() {

        ActivityDao activityDao = new ActivityDao(sessionFactory);

        UserDao userDao = new UserDao(sessionFactory);

        String prefix = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        configuration.reportFolder.mkdirs();

        Session session = sessionFactory.openSession();
        ManagedSessionContext.bind(session);

        try {
            List<ActivityMetadata> activities = new ArrayList<>();
            runInTransaction(session, () -> {
                activityDao.all().stream().map(activity -> new ActivityMetadata(activity)).forEach(activityMetadata -> activities.add(activityMetadata));
            });

            if (configuration.simplifyVocabulary) {
                LOGGER.info("Simplifying vocabulary");

                Simplifier.Metadata metadata = simplifyVocabulary(activities, configuration.simplifyRules, configuration.minimumWordGroupSize);

                if (configuration.reportFilesRetentionLimit > 0) {
                    transformReport(metadata, "simplifications.xsl", new File(configuration.reportFolder, prefix + ".SimplifierMetadata.html"));
                }
            }

            ActivityMetadataComparatorWeight[] comparatorWeights = new ActivityMetadataComparatorWeight[]{
                    new ActivityMetadataComparatorWeight(new AllTextComparator(), configuration.comparatorFactorAllText),
                    new ActivityMetadataComparatorWeight(new NameTextComparator(), configuration.comparatorFactorName),
                    new ActivityMetadataComparatorWeight(new MaterialTextComparator(), configuration.comparatorFactorMaterials),
                    new ActivityMetadataComparatorWeight(new IntroductionTextComparator(), configuration.comparatorFactorIntroduction),
                    new ActivityMetadataComparatorWeight(new CategoryComparator(), configuration.comparatorFactorCategories),
                    new ActivityMetadataComparatorWeight(new AgeComparator(), configuration.comparatorFactorAge),
                    new ActivityMetadataComparatorWeight(new ParticipantCountComparator(), configuration.comparatorFactorParticipantCount),
                    new ActivityMetadataComparatorWeight(new TimeComparator(), configuration.comparatorFactorTime)
            };

            LOGGER.info("Generating reports");

            Report report = new Report();
            report.comparatorValuesLabels = createComparatorValuesLabels(comparatorWeights);

            final User crawlerUser = DataAccessUtils.getUser(userDao, configuration.getRelationOwner());

            session.clear();

            runInTransaction(session, () -> {
                long lastFreeMemory = Runtime.getRuntime().freeMemory();
                for (int i = 0; i < activities.size(); i++) {

                    ActivityMetadata metadata = activities.get(i);

                    long freeMemory = Runtime.getRuntime().freeMemory();
                    LOGGER.info(String.format("Calculating related activities for activity %5d, %3d percent done. Memory: total %10d, max %10d, free %10d, free diff %12d",
                            metadata.id,
                            (int) (100.0 * i / activities.size()),
                            Runtime.getRuntime().totalMemory(),
                            Runtime.getRuntime().maxMemory(),
                            freeMemory,
                            freeMemory - lastFreeMemory));

                    ActivityComparisonResult[] comparisons = getComparisonValues(activities, comparatorWeights, i);

                    Report.Activity relation = createReportRelation(activities, configuration.maxRelated, comparisons, metadata);

                    report.activities.add(relation);

                    activityDao.setActivityRelations(crawlerUser,
                            relation.id,
                            relation.relations.stream()
                                    .map(relation1 -> Long.valueOf(relation1.id))
                                    .toArray(length -> new Long[length]));

                    checkMemoryConsumtion();

                    if (System.currentTimeMillis() > abortTime) {
                        LOGGER.info("The job's time limit has been exceeded and no more activities will be processed");
                        break;
                    }

                    Thread.currentThread().yield();

                    lastFreeMemory = freeMemory;
                }
            });

            checkMemoryConsumtion();

            if (configuration.reportFilesRetentionLimit > 0) {
                transformReport(report, "report.xsl", new File(configuration.reportFolder, prefix + ".Report.html"));
                transformReport(report, "report-simpletext.xsl", new File(configuration.reportFolder, prefix + ".SimpleReport.txt"));
            }

        } catch (Throwable e) {
            LOGGER.warn("Exception when calculating similar activities.", e);
        } finally {
            if (session != null) {
                session.close();
            }
            purgeReportFiles();
        }
    }

    protected void purgeReportFiles() {
        if (configuration.reportFilesRetentionLimit > 0) {
            try {
                Files.list(configuration.reportFolder.toPath())
                        .map(p -> p.toFile())
                        .sorted((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()))
                        .skip(configuration.reportFilesRetentionLimit * NUMBER_OF_REPORT_FILES_PER_RUN)
                        .forEach(File::delete);
                LOGGER.debug("Removed old report files");
            } catch (IOException e) {
                LOGGER.warn("Could not remove old report files", e);
            }
        }
    }

    protected void checkMemoryConsumtion() {
        if (1.0 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory() < FREE_MEMORY_LIMIT) {
            LOGGER.info("Free memory is less than 30% of the total memory. Application will request the JVM to perform garbage collection.");
            System.gc();
        }
    }

    private void runInTransaction(Session session, Runnable runnable) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            runnable.run();

            tx.commit();
        } catch (Exception e) {
            LOGGER.warn("Exception when calculating similar activities.", e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    public Simplifier.Metadata simplifyVocabulary(List<ActivityMetadata> activities, SimplifyRule[] simplifyRules, int minimumWordGroupSize) throws IOException, JAXBException {
        Simplifier simplifier = new Simplifier(getAllWords(activities), simplifyRules, minimumWordGroupSize);

        for (ActivityMetadata activityMetadata : activities) {
            activityMetadata.simplifyVocabulary(simplifier);
        }

        return simplifier.getMetadata();
    }

    private List<String> getAllWords(List<ActivityMetadata> activities) {
        ArrayList<String> allWords = new ArrayList<>();
        for (ActivityMetadata revision : activities) {
            Collections.addAll(allWords, revision.getAllWords());
        }
        return allWords;
    }

    private Report.Activity createReportRelation(List<ActivityMetadata> activities, int maxRelated, ActivityComparisonResult[] comparisons, ActivityMetadata currentRevision) {
        Report.Activity relation = new Report.Activity(
                currentRevision.name,
                currentRevision.id);

        Arrays.sort(comparisons, (o1, o2) -> Double.compare(o2.comparatorValues[0], o1.comparatorValues[0]));

        for (int i = 0; i < Math.min(comparisons.length, maxRelated); i++) {
            ActivityComparisonResult comparison = comparisons[i];
            String[] formattedComparisonValues = new String[comparison.comparatorValues.length];
            for (int j = 0; j < comparison.comparatorValues.length; j++) {
                Double comparisonValue = comparison.comparatorValues[j];
                formattedComparisonValues[j] = NUMBER_FORMAT.format(comparisonValue);
            }
            ActivityMetadata activity = activities.get(comparison.activityId);
            relation.add(new Report.Activity.Relation(
                    createComparatorValues(comparison.comparatorValues),
                    activity.name,
                    activity.id
            ));
        }
        return relation;
    }

    private static class ActivityComparisonResult {
        private int activityId;
        private double[] comparatorValues;

        public ActivityComparisonResult(int activityId, double[] comparatorValues) {
            this.activityId = activityId;
            this.comparatorValues = comparatorValues;
        }
    }

    private ActivityComparisonResult[] getComparisonValues(List<ActivityMetadata> allActivities, ActivityMetadataComparatorWeight[] comparators, int sourceActivityIndex) {
        ActivityMetadata sourceActivityMetadata = allActivities.get(sourceActivityIndex);
        ActivityComparisonResult[] comparisons = new ActivityComparisonResult[allActivities.size()];
        for (int j = 0; j < allActivities.size(); j++) {
            double[] compare = new double[1 + comparators.length];
            if (sourceActivityIndex != j) {
                compare[0] = 0.0;
                int x = 0;
                for (ActivityMetadataComparatorWeight entry1 : comparators) {
                    compare[++x] = entry1.weight > 0 ? entry1.weight * entry1.comparator.compare(sourceActivityMetadata, allActivities.get(j)) : 0;
                    compare[0] += compare[x];
                }
            } else {
                Arrays.fill(compare, 0.0);
            }
            comparisons[j] = new ActivityComparisonResult(j, compare);
        }
        return comparisons;
    }

    private void transformReport(Object o, String xslResourceName, File outputFile) throws ParserConfigurationException, JAXBException, TransformerException, FileNotFoundException {
        Marshaller reportMarshaller = null;
        reportMarshaller = JAXBContext.newInstance(o.getClass()).createMarshaller();
        reportMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        if (xslResourceName != null) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream(xslResourceName)));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            reportMarshaller.marshal(o, doc);
            transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outputFile)));
        } else {
            reportMarshaller.marshal(o, outputFile);
        }
    }

    private String[] createComparatorValuesLabels(ActivityMetadataComparatorWeight[] comparators) {
        String[] strings = new String[1 + comparators.length];
        int x = 0;
        strings[x++] = "=";
        for (ActivityMetadataComparatorWeight comparator : comparators) {
            strings[x++] = comparator.comparator.toString();
        }
        return strings;
    }

    private String[] createComparatorValues(double[] values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = NUMBER_FORMAT.format(values[i]);
        }
        return strings;
    }
}
