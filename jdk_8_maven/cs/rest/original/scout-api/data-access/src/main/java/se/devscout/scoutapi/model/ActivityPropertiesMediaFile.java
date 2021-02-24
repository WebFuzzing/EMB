package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ACTIVITY_PROPERTIES_MEDIA_FILE")
@XmlRootElement
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityPropertiesMediaFile {

    public ActivityPropertiesMediaFile() {
    }

    public ActivityPropertiesMediaFile(ActivityProperties activityProperties, MediaFile mediaFile) {
        this(activityProperties, mediaFile, false);
    }

    public ActivityPropertiesMediaFile(ActivityProperties activityProperties, MediaFile mediaFile, boolean featured) {
        this.activityProperties = activityProperties;
        this.mediaFile = mediaFile;
        this.featured = featured;
    }

    @EmbeddedId
    private Key key = new Key();

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_properties_id")
    @MapsId(value = "activityPropertiesId")
    private ActivityProperties activityProperties;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_file_id")
    @MapsId(value = "mediaFileId")
    private MediaFile mediaFile;

    @Basic
    private boolean featured;

    public ActivityProperties getActivityProperties() {
        return activityProperties;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityPropertiesMediaFile)) return false;
        ActivityPropertiesMediaFile that = (ActivityPropertiesMediaFile) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Embeddable
    public static class Key implements Serializable {
        private long activityPropertiesId;
        private long mediaFileId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(activityPropertiesId, key.activityPropertiesId) &&
                    Objects.equals(mediaFileId, key.mediaFileId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityPropertiesId, mediaFileId);
        }

        public long getActivityPropertiesId() {
            return activityPropertiesId;
        }

        public void setActivityPropertiesId(long activityPropertiesId) {
            this.activityPropertiesId = activityPropertiesId;
        }

        public long getMediaFileId() {
            return mediaFileId;
        }

        public void setMediaFileId(long mediaFileId) {
            this.mediaFileId = mediaFileId;
        }
    }

    @Override
    public String toString() {
        return "mediaFile=" + mediaFile;
    }
}
