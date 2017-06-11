package se.devscout.scoutapi.dao;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.model.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityDao extends AbstractDAO<Activity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityDao.class);

    public enum SortOrder {
        name("prop.name"),
        favouritesCount("a.derived.favouritesCount");

        private final String orderBy;

        SortOrder(String orderBy) {
            this.orderBy = orderBy;
        }
    }

    private final TagDao tagDao;
    private final MediaFileDao mediaFileDao;

    public ActivityDao(SessionFactory sessionFactory) {
        super(sessionFactory);
        tagDao = new TagDao(sessionFactory);
        mediaFileDao = new MediaFileDao(sessionFactory);
    }

    public List<Activity> all() {
        return Lists.newArrayList(new HashSet<>(list(namedQuery("Activity.all"))));
    }

    public List<Activity> all(String text,
                              boolean onlyNameText,
                              Boolean featured,
                              List<Long> tags,
                              List<Integer> ages,
                              List<Integer> participants,
                              List<Integer> durations,
                              List<Long> ids,
                              Integer random,
                              Long ratingsCountMin,
                              Double ratingsAverageMin,
                              User favouriteOfUser,
                              SortOrder sortOrder,
                              Integer maxResults) {
        StringBuilder q = new StringBuilder();
        List<String> qWhereConditions = new ArrayList<>();
        Map<String, Object> qWhereParameters = new HashMap<>();
        q.append("SELECT a FROM Activity a JOIN FETCH a.publishedProperties prop");

        if (text != null) {
            Splitter.on(' ').splitToList(text).stream().map(s -> s.trim()).forEach(t -> {
                boolean negate = false;
                if (t.charAt(0) == '-') {
                    t = t.substring(1);
                    negate = true;
                }
                int uniqueIndex = qWhereConditions.size();

                List<String> fields = onlyNameText ? Collections.singletonList("name") : Arrays.asList("name", "descriptionMaterial", "descriptionIntroduction", "descriptionPrepare", "descriptionMain", "descriptionSafety", "descriptionNotes");
                String expr = fields.stream().map(s -> "UPPER(prop." + s + ") LIKE :text" + uniqueIndex).collect(Collectors.joining(" OR "));

                qWhereConditions.add(negate ? "NOT(" + expr + ")" : expr);
                qWhereParameters.put("text" + uniqueIndex, "%" + t.toUpperCase() + "%");
            });
        }
        if (ages != null) {
            ages.stream().forEach(age -> {
                int uniqueIndex = qWhereConditions.size();
                qWhereParameters.put("age" + uniqueIndex, age);
                qWhereConditions.add("prop.ageMin <= :age" + uniqueIndex);
                qWhereConditions.add(":age" + uniqueIndex + " <= prop.ageMax");
            });
        }
        if (participants != null) {
            participants.stream().forEach(participant -> {
                int uniqueIndex = qWhereConditions.size();
                qWhereParameters.put("participants" + uniqueIndex, participant);
                qWhereConditions.add("prop.participantsMin <= :participants" + uniqueIndex);
                qWhereConditions.add(":participants" + uniqueIndex + " <= prop.participantsMax");
            });
        }
        if (durations != null) {
            durations.stream().forEach(duration -> {
                int uniqueIndex = qWhereConditions.size();
                qWhereParameters.put("duration" + uniqueIndex, duration);
                qWhereConditions.add("prop.timeMin <= :duration" + uniqueIndex);
                qWhereConditions.add(":duration" + uniqueIndex + " <= prop.timeMax");
            });
        }
        if (ids != null) {
            qWhereConditions.add("a.id IN (" + Joiner.on(',').join(ids) + ")");
        }
        if (tags != null) {
            qWhereConditions.add("EXISTS(SELECT t FROM prop.tags t WHERE t.tag.id IN (" + Joiner.on(',').join(tags) + "))");
        }
        if (featured != null) {
            qWhereConditions.add("prop.featured = :featured");
            qWhereParameters.put("featured", featured);
        }
        if (ratingsAverageMin != null) {
            qWhereConditions.add("a.derived.ratingsAverage >= :ratingsAverage");
            qWhereParameters.put("ratingsAverage", ratingsAverageMin);
        }
        if (ratingsCountMin != null) {
            qWhereConditions.add("a.derived.ratingsCount >= :ratingsCount");
            qWhereParameters.put("ratingsCount", ratingsCountMin);
        }
        if (favouriteOfUser != null) {
            qWhereConditions.add("EXISTS(SELECT ar FROM ActivityRating ar WHERE ar.favourite = TRUE AND a = ar.activity AND ar.user = :user)");
            qWhereParameters.put("user", favouriteOfUser);
        }
        if (!qWhereConditions.isEmpty()) {
            q.append(" WHERE (" + Joiner.on(") AND (").join(qWhereConditions) + ")");
        }
        if (sortOrder != null) {
            q.append(" ORDER BY " + sortOrder.orderBy);
        }
        Query query = currentSession().createQuery(q.toString());
        if (maxResults != null && maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        qWhereParameters.forEach((key, val) -> {
            query.setParameter(key, val);
        });

        List<Activity> activities = Lists.newArrayList(new HashSet<>(query.list()));
        if (0 < random && random < activities.size()) {
            Set<Integer> randomIds = new HashSet<>();
            while (randomIds.size() < random) {
                randomIds.add((int) (Math.random() * activities.size()));
            }
            List<Activity> randomActivities = new ArrayList<>();
            for (Integer id : randomIds) {
                randomActivities.add(activities.get(id));
            }
            activities = randomActivities;
        }
        return activities;
    }

    public Activity read(long id) {
        return get(id);
    }

    public Activity create(Activity activity, boolean autoCreateTags, boolean autoCreateMediaFiles) {
        initTags(activity.getProperties(), autoCreateTags);
        initMediaFiles(activity.getProperties(), autoCreateMediaFiles);
        return persist(activity);
    }

    public Activity update(Activity activity, ActivityProperties newValues, boolean autoCreateTags, boolean autoCreateMediaFiles, boolean patch) {
        if (activity.getProperties().isContentEqual(newValues)) {
            LOGGER.info("Will not update activity " + activity.getId() + " because the current values and the updated values are identical.");
            return activity;
        }
        initTags(newValues, autoCreateTags);
        initMediaFiles(newValues, autoCreateMediaFiles);

        unsetPublishedProperties(activity);

        activity.updateProperties(newValues, patch);
        return persist(activity);
    }

    private void unsetPublishedProperties(Activity activity) {
        ActivityProperties prevProps = activity.getProperties();
        prevProps.setPublishingActivity(null);
        currentSession().saveOrUpdate(prevProps); // Need to save/update (i.e. clear) activity reference for current properties object to prevent unique-key exception when saving the new properties object.
        currentSession().flush(); // TODO: Is flush() necessary?
    }

    private void initTags(ActivityProperties props, boolean autoCreate) {
        props.setTags(props.getTags() != null ? props.getTags().stream().map(tag -> {
            // TODO: tagDao.read seems to cause unnecessary SELECTs from ActivityPropertiesTag when performing the first search
            Tag persistedTag = tag.getId() != 0 ? tagDao.read(tag.getId()) : tagDao.read(tag.getGroup(), tag.getName());
            if (persistedTag != null) {
                return persistedTag;
            } else {
                if (autoCreate) {
                    return tagDao.create(tag);
                }
                throw new EntityNotFoundException("Could not find tag with id " + tag.getId());
            }
        }).collect(Collectors.toSet()) : null);
    }

    private void initMediaFiles(ActivityProperties props, boolean autoCreate) {
        props.setMediaFiles(props.getMediaFiles() != null ? props.getMediaFiles().stream().map(mediaFile -> {
            MediaFile persistedMediaFile = mediaFile.getId() != 0 ? mediaFileDao.read(mediaFile.getId()) : mediaFileDao.read(mediaFile.getUri());
            if (persistedMediaFile != null) {
                return persistedMediaFile;
            } else {
                if (autoCreate) {
                    return mediaFileDao.create(mediaFile);
                }
                throw new EntityNotFoundException("Could not find media file with id " + mediaFile.getId());
            }
        }).collect(Collectors.toSet()) : null);
    }

    public void delete(Activity activity) {
        // TODO: Why aren't these associations automatically deleted using CASCADE?
        int deletedRelations = namedQuery("ActivityRelation.deleteForActivity").setParameter("activity", activity).executeUpdate();
        LOGGER.info("Deleted " + deletedRelations + " relations related to activity " + activity.getId());
        int deletedRatings = namedQuery("ActivityRating.deleteByActivity").setParameter("activity", activity).executeUpdate();
        LOGGER.info("Deleted " + deletedRatings + " ratings related to activity " + activity.getId());

        currentSession().delete(activity);
    }

    public List<Long> getRelatedActivityIds(long sourceId) {
        return namedQuery("ActivityRelation.relatedActivityIds").setParameter("sourceId", sourceId).list();
    }

    public void setActivityRelations(User owner, long sourceId, Long... targetActivityIds) {

        List<Long> newIds = Arrays.asList(targetActivityIds);
        List<Long> currentIds = getRelatedActivityIds(sourceId);

        boolean isNewListEqualCurrentList = Stream.of(targetActivityIds).allMatch(currentIds::contains) && currentIds.stream().allMatch(newIds::contains);

        if (!isNewListEqualCurrentList) {
            Activity source = read(sourceId);

            source.getRelations().clear();
            currentSession().flush();
            source.getRelations().addAll(Stream.of(targetActivityIds).map(targetActivityId -> new ActivityRelation(source, read(targetActivityId), owner)).collect(Collectors.toList()));

            persist(source);

            LOGGER.info("Activity {} is now related to: {}. Was related to: {}", source.getId(), newIds, currentIds);
        } else {
            LOGGER.debug("Ignoring request to update list of related activities. The lists are the same. {} = {}", currentIds, newIds);
        }
    }
}
