package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ACTIVITY_PROPERTIES_TAG")
@XmlRootElement
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityPropertiesTag {

    public ActivityPropertiesTag() {
    }

    public ActivityPropertiesTag(ActivityProperties activityProperties, Tag tag) {
        this.activityProperties = activityProperties;
        this.tag = tag;
    }

    @EmbeddedId
    private Key key = new Key();

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_properties_id")
    @MapsId(value = "activityPropertiesId")
    private ActivityProperties activityProperties;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id")
    @MapsId(value = "tagId")
    private Tag tag;


    public ActivityProperties getActivityProperties() {
        return activityProperties;
    }

    public Tag getTag() {
        return tag;
    }

    @Embeddable
    public static class Key implements Serializable {
        private long activityPropertiesId;
        private long tagId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(activityPropertiesId, key.activityPropertiesId) &&
                    Objects.equals(tagId, key.tagId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityPropertiesId, tagId);
        }

        public long getActivityPropertiesId() {
            return activityPropertiesId;
        }

        public void setActivityPropertiesId(long activityPropertiesId) {
            this.activityPropertiesId = activityPropertiesId;
        }

        public long getTagId() {
            return tagId;
        }

        public void setTagId(long tagId) {
            this.tagId = tagId;
        }
    }

    @Override
    public String toString() {
        return "tag=" + tag;
    }
}
