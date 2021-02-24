package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ACTIVITY_RELATION")
@NamedQueries({
        @NamedQuery(name = "ActivityRelation.deleteByUser", query = "DELETE FROM ActivityRelation ar WHERE ar.owner = :owner"),
        @NamedQuery(name = "ActivityRelation.deleteBySource", query = "DELETE FROM ActivityRelation ar WHERE ar.activity1 = :src")
})
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityRelation {

    public ActivityRelation() {
    }

    public ActivityRelation(Activity activity1, Activity activity2, User owner) {
        this.activity1 = activity1;
        this.activity2 = activity2;
        this.owner = owner;
    }

    @EmbeddedId
    private Key key = new Key();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_1_id")
    @MapsId(value = "activity1Id")
    private Activity activity1;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_2_id")
    @MapsId(value = "activity2Id")
    private Activity activity2;

    @ManyToOne(optional = false)
    private User owner;

    public Activity getActivity1() {
        return activity1;
    }

    public Activity getActivity2() {
        return activity2;
    }

    public long getActivity2Id() {
        return key.activity2Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityRelation)) return false;
        ActivityRelation that = (ActivityRelation) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Embeddable
    public static class Key implements Serializable {
        private long activity1Id;
        private long activity2Id;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(activity1Id, key.activity1Id) &&
                    Objects.equals(activity2Id, key.activity2Id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activity1Id, activity2Id);
        }

        public long getActivity1Id() {
            return activity1Id;
        }

        public void setActivity1Id(long activity1Id) {
            this.activity1Id = activity1Id;
        }

        public long getActivity2Id() {
            return activity2Id;
        }

        public void setActivity2Id(long activity2Id) {
            this.activity2Id = activity2Id;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "activity1Id=" + activity1Id +
                    ", activity2Id=" + activity2Id +
                    '}';
        }
    }
}
