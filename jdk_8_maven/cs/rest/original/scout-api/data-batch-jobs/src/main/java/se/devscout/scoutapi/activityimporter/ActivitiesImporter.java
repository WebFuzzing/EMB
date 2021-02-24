package se.devscout.scoutapi.activityimporter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.AbstractBatchJob;
import se.devscout.scoutapi.dao.ActivityDao;
import se.devscout.scoutapi.dao.DataAccessUtils;
import se.devscout.scoutapi.dao.MediaFileDao;
import se.devscout.scoutapi.dao.UserDao;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.MediaFile;
import se.devscout.scoutapi.model.User;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivitiesImporter extends AbstractBatchJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiesImporter.class);
    private File tempFolder;
    private String crawlerUser;
    private SessionFactory sessionFactory;
    private long abortTime;

    public ActivitiesImporter(File tempFolder, String crawlerUser, SessionFactory sessionFactory, long abortTime) {
        this.tempFolder = tempFolder;
        this.crawlerUser = crawlerUser;
        this.sessionFactory = sessionFactory;
        this.abortTime = abortTime;
    }

    @Override
    public void run() {
        ActivityDao activityDao = new ActivityDao(sessionFactory);
        UserDao userDao = new UserDao(sessionFactory);
        MediaFileDao mediaFileDao = new MediaFileDao(sessionFactory);

        try {
            DataAccessUtils.runInTransaction(sessionFactory, () -> {

                List<ActivityProperties> remoteActivities = new AktivitetsbankenCrawler(new DefaultWebPageLoader(tempFolder)).readAllActivities();

                Map<String, List<Activity>> localActivitiesByURL = getLocalActivities(activityDao);

                User crawlerUser = DataAccessUtils.getUser(userDao, this.crawlerUser);

                for (ActivityProperties remoteActivity : remoteActivities) {
                    remoteActivity.setAuthor(crawlerUser);

                    // Try to find suitable media files (images) to illustrate the activity. Do this by looking for media
                    // resources with keywords matching the name of the current activity.
                    if (remoteActivity.getMediaFiles().isEmpty()) {
                        String activitiyKeyword = MediaFile.getSimplifiedKeyword(remoteActivity.getName());
                        List<MediaFile> imagesBasedOnActivityName = mediaFileDao.byKeyword(activitiyKeyword);
                        if (!imagesBasedOnActivityName.isEmpty()) {
                            remoteActivity.setMediaFiles(imagesBasedOnActivityName);
                        }
                    }

                    String remoteSource = remoteActivity.getSource();
                    if (localActivitiesByURL.containsKey(remoteSource)) {
                        List<Activity> activitiesForRemoteSource = localActivitiesByURL.get(remoteSource);
                        if (activitiesForRemoteSource.size() == 1) {
                            Activity activity = activitiesForRemoteSource.get(0);
                            if (!activity.getProperties().isContentEqual(remoteActivity)) {
                                activityDao.update(activity, remoteActivity, true, true, false);
                                LOGGER.info("Updated activity {} based on {}", activity.getId(), remoteSource);
                            } else {
                                LOGGER.debug("Ignored activity {} because no properties would have been updated.", activity.getId());
                            }
                        } else {
                            LOGGER.info("For some reason, there are multiple activities created from this URL: {}", remoteSource);
                        }
                    } else {
                        Activity createdActivity = activityDao.create(new Activity(remoteActivity), true, true);
                        LOGGER.info("Created activity {} based on {}", createdActivity.getId(), remoteSource);
                    }

                    if (System.currentTimeMillis() > abortTime) {
                        LOGGER.info("The job's time limit has been exceeded and no more activities will be processed");
                        break;
                    }
                }
                return null;
            });
        } catch (Exception e) {
            LOGGER.warn("Exception when crawling Aktivitetsbanken.", e);
        }
    }

    private Map<String, List<Activity>> getLocalActivities(ActivityDao activityDao) {
        List<Activity> localActivities = activityDao.all();
        return localActivities.stream()
                .filter(activity -> StringUtils.isNotEmpty(activity.getProperties().getSource()))
                .collect(Collectors.groupingBy(activity -> activity.getProperties().getSource()));
    }

    public static class Task extends io.dropwizard.servlets.tasks.Task {
        private File crawlerTempFolder;
        private String crawlerUser;
        private SessionFactory sessionFactory;

        public Task(File crawlerTempFolder, String crawlerUser, SessionFactory sessionFactory) {
            super("auto-import");
            this.crawlerTempFolder = crawlerTempFolder;
            this.crawlerUser = crawlerUser;
            this.sessionFactory = sessionFactory;
        }

        @Override
        public void execute(ImmutableMultimap<String, String> params, PrintWriter printWriter) throws Exception {
            int maxExecutionTimeSeconds = getMaxExecutionTime(params);
            long abortTime = System.currentTimeMillis() + (maxExecutionTimeSeconds * 1000);
            new ActivitiesImporter(
                    crawlerTempFolder,
                    crawlerUser,
                    sessionFactory,
                    abortTime)
                    .run();

        }

        private int getMaxExecutionTime(ImmutableMultimap<String, String> params) {
            int maxExecutionTimeSeconds = 60;
            ImmutableCollection<String> customLimit = params.get("maxExecutionTimeSeconds");
            if (!customLimit.isEmpty()) {
                String max = customLimit.asList().get(0);
                try {
                    maxExecutionTimeSeconds = Integer.parseInt(max);
                } catch (NumberFormatException e) {
                    LOGGER.info("Someone tried to start task 'auto-import' with an incorrect parameter value: {}", max);
                }
            }
            return maxExecutionTimeSeconds;
        }
    }
}
