package se.devscout.scoutapi.dao;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityRating;
import se.devscout.scoutapi.model.User;

import java.util.List;

public class ActivityRatingDao extends AbstractDAO<ActivityRating> {
    public ActivityRatingDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<ActivityRating> all() {
        return namedQuery("ActivityRating.all").list();
    }

    public List<ActivityRating> all(Activity activity) {
        return namedQuery("ActivityRating.byActivity").setParameter("activity", activity).list();
    }

    public List<ActivityRating> all(User user) {
        return namedQuery("ActivityRating.byUser").setParameter("user", user).list();
    }

    public ActivityRating read(Activity activity, User user) {
        return get(new ActivityRating.Key(activity, user));
    }

    public void create(ActivityRating activityRating) {
        persist(activityRating);
    }

    public void update(ActivityRating activityRating) {
        persist(activityRating);
    }

    public void delete(ActivityRating activityRating) {
        currentSession().delete(activityRating);
    }

}
