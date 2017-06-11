package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ACTIVITY_RATING")
@NamedQueries(value = {
        @NamedQuery(name = "ActivityRating.all", query = "" +
                "SELECT ar " +
                "FROM ActivityRating ar"),
        @NamedQuery(name = "ActivityRating.byActivity", query = "" +
                "SELECT ar " +
                "FROM ActivityRating AS ar " +
                "WHERE ar.activity = :activity"),
        @NamedQuery(name = "ActivityRating.byUser", query = "" +
                "SELECT ar " +
                "FROM ActivityRating AS ar " +
                "WHERE ar.user = :user"),
        @NamedQuery(name = "ActivityRating.deleteByActivity", query = "" +
                "DELETE FROM ActivityRating ar " +
                "WHERE ar.activity = :activity")
})

@XmlRootElement
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityRating extends ActivityRatingAttrs {

    public ActivityRating() {
    }

    public ActivityRating(Activity activity, User user) {
        this(activity, user, 0, false);
    }

    public ActivityRating(Activity activity, User user, Integer rating, Boolean favourite) {
        super(rating, favourite);
        this.activity = activity;
        this.user = user;
    }

    @EmbeddedId
    private Key key = new Key();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    @MapsId(value = "activityId")
    private Activity activity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId(value = "userId")
    private User user;

    public Activity getActivity() {
        return activity;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityRating)) return false;
        ActivityRating that = (ActivityRating) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Embeddable
    public static class Key implements Serializable {
        private long activityId;
        private long userId;

        public Key() {
        }

        public Key(Activity activity, User user) {
            this(activity.getId(), user.getId());
        }

        public Key(long activityId, long userId) {
            this.activityId = activityId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(activityId, key.activityId) &&
                    Objects.equals(userId, key.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityId, userId);
        }

        public long getActivityId() {
            return activityId;
        }

        public void setActivityId(long activityId) {
            this.activityId = activityId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }
    }
}
