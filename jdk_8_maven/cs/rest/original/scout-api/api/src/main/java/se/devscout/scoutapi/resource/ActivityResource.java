package se.devscout.scoutapi.resource;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.ActivityDao;
import se.devscout.scoutapi.dao.ActivityRatingDao;
import se.devscout.scoutapi.model.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ActivityResource extends AbstractResource {

    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private final ActivityDao dao;
    private final ActivityRatingDao activityRatingDao;

    public ActivityResource(ActivityDao dao, ActivityRatingDao activityRatingDao) {
        this.dao = dao;
        this.activityRatingDao = activityRatingDao;
    }

    protected List<Activity> getActivities(String name,
                                           String text,
                                           Boolean featured,
                                           String tagIds,
                                           String ages,
                                           String numberOfParticipants,
                                           String durations,
                                           String activityIds,
                                           Integer random,
                                           Long ratingsCountMin,
                                           Double ratingsAverageMin,
                                           User userFavourite,
                                           ActivityDao.SortOrder sortOrder,
                                           Integer maxResults) {
        List<Long> tags = null;
        List<Integer> ages1 = null;
        List<Integer> participants = null;
        List<Integer> durations1 = null;
        List<Long> ids = null;
        try {
            tags = Strings.isNullOrEmpty(tagIds) ? null : COMMA_SPLITTER.splitToList(tagIds).stream().map(Long::valueOf).collect(Collectors.toList());
            ages1 = Strings.isNullOrEmpty(ages) ? null : COMMA_SPLITTER.splitToList(ages).stream().map(Integer::valueOf).collect(Collectors.toList());
            participants = Strings.isNullOrEmpty(numberOfParticipants) ? null : COMMA_SPLITTER.splitToList(numberOfParticipants).stream().map(Integer::valueOf).collect(Collectors.toList());
            durations1 = Strings.isNullOrEmpty(durations) ? null : COMMA_SPLITTER.splitToList(durations).stream().map(Integer::valueOf).collect(Collectors.toList());
            ids = Strings.isNullOrEmpty(activityIds) ? null : COMMA_SPLITTER.splitToList(activityIds).stream().map(Long::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            throw new WebApplicationException("Could not understand the request. Perhaps you sent text instead of integers?", Response.Status.BAD_REQUEST);
        }
        return dao.all(
                Strings.isNullOrEmpty(text) ? name : text,
                Strings.isNullOrEmpty(text),
                featured,
                tags,
                ages1,
                participants,
                durations1,
                ids,
                random,
                ratingsCountMin,
                ratingsAverageMin,
                userFavourite,
                sortOrder,
                maxResults
                );
    }

    public void delete(AuthResult authResult, HttpServletResponse response, long id) {
        doAuth(authResult, response, Permission.activity_edit);
        dao.delete(dao.read(id));
    }

    public Activity create(AuthResult authResult, HttpServletResponse response, ActivityProperties properties) {
        doAuth(authResult, response, Permission.activity_create);
        Activity activity = new Activity(properties);
        Activity created = dao.create(activity,
                Permission.category_create.isGrantedTo(authResult.getUser()),
                Permission.mediaitem_create.isGrantedTo(authResult.getUser()));
        return created;
    }

    public Response get(long id, String attrs) {
        return okResponse(dao.read(id), attrs);
    }

    public Response getRating(AuthResult authResult, HttpServletResponse response, long id, String attrs) {
        doAuth(authResult, response, Permission.rating_set_own);
        ActivityRatingAttrs activityRating = activityRatingDao.read(dao.read(id), authResult.getUser());
        return okResponse(activityRating, attrs);
    }

    public void postRating(AuthResult authResult, HttpServletResponse response, long id, ActivityRatingAttrs attrs) {
        doAuth(authResult, response, Permission.rating_set_own);
        Activity activity = dao.read(id);
        User user = authResult.getUser();
        ActivityRating activityRating = activityRatingDao.read(activity, user);
        if (activityRating != null) {
            if (attrs.getRating() != null) {
                activityRating.setRating(attrs.getRating());
            }
            if (attrs.isFavourite() != null) {
                activityRating.setFavourite(attrs.isFavourite());
            }
            activityRatingDao.update(activityRating);
        } else {
            activityRating = new ActivityRating(activity, user, attrs.getRating(), attrs.isFavourite());
            activityRatingDao.create(activityRating);
        }
    }

    public void deleteRating(AuthResult authResult, HttpServletResponse response, long id) {
        doAuth(authResult, response, Permission.rating_set_own);
        ActivityRating activityRating = activityRatingDao.read(dao.read(id), authResult.getUser());
        if (activityRating.isFavourite() != null && activityRating.isFavourite()) {
            // Activity should be kept as favourite but rating should be removed. Keep entity.
            activityRating.setRating(null);
            activityRatingDao.update(activityRating);
        } else {
            // Activity has only been rated and it is safe to remove the entity.
            activityRatingDao.delete(activityRating);
        }
    }

    public Activity update(AuthResult authResult, HttpServletResponse response, long id, ActivityProperties properties) {
        doAuth(authResult, response, Permission.activity_edit);
        Activity persisted = dao.read(id);
        dao.update(persisted,
                properties,
                Permission.category_create.isGrantedTo(authResult.getUser()),
                Permission.mediaitem_create.isGrantedTo(authResult.getUser()),
                false);
        return persisted;
    }

    public Activity patch(AuthResult authResult, HttpServletResponse response, long id, ActivityProperties properties) {
        doAuth(authResult, response, Permission.activity_edit);
        Activity persisted = dao.read(id);
        dao.update(persisted,
                properties,
                Permission.category_create.isGrantedTo(authResult.getUser()),
                Permission.mediaitem_create.isGrantedTo(authResult.getUser()),
                true);
        return persisted;
    }
}
