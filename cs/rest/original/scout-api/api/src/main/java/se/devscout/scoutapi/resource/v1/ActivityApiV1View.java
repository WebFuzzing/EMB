package se.devscout.scoutapi.resource.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.MediaFile;
import se.devscout.scoutapi.model.Tag;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityApiV1View {
    private final Activity activity;
    private final ActivityProperties props;

    public ActivityApiV1View(Activity activity) {
        this.activity = activity;
        this.props = activity.getProperties();
    }

    @JsonProperty("_related")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<RelatedActivityIdView> getRelatedIds() {
        return Stream.of(activity.getRelatedIds()).map(aLong -> new RelatedActivityIdView(aLong)).collect(Collectors.toList());
    }

    @JsonProperty("_ratings_average")
    public double getRatingsAverage() {
        return activity.getRatingsAverage();
    }

    public long getRatingsSum() {
        return activity.getRatingsSum();
    }

    public long getRatingsCount() {
        return activity.getRatingsCount();
    }

    public String getName() {
        return props.getName();
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.S'Z'")
    @JsonProperty("updated_at")
    public Date getDateUpdated() {
        return props.getDateUpdated();
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.S'Z'")
    @JsonProperty("created_at")
    public Date getDateCreated() {
        return props.getDateCreated();
    }

    @JsonProperty("descr_material")
    public String getDescriptionMaterial() {
        return props.getDescriptionMaterial();
    }

    @JsonProperty("descr_introduction")
    public String getDescriptionIntroduction() {
        return props.getDescriptionIntroduction();
    }

    @JsonProperty("descr_prepare")
    public String getDescriptionPrepare() {
        return props.getDescriptionPrepare();
    }

    @JsonProperty("descr_main")
    public String getDescriptionMain() {
        return props.getDescriptionMain();
    }

    @JsonProperty("descr_safety")
    public String getDescriptionSafety() {
        return props.getDescriptionSafety();
    }

    public String getDescriptionNotes() {
        return props.getDescriptionNotes();
    }

    public Integer getAgeMin() {
        return props.getAgeMin();
    }

    public Integer getAgeMax() {
        return props.getAgeMax();
    }

    public Integer getParticipantsMin() {
        return props.getParticipantsMin();
    }

    public Integer getParticipantsMax() {
        return props.getParticipantsMax();
    }

    public Integer getTimeMin() {
        return props.getTimeMin();
    }

    public Integer getTimeMax() {
        return props.getTimeMax();
    }

    public Boolean isFeatured() {
        return props.isFeatured();
    }

    @JsonProperty("categories")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Tag> getTags() {
        return props.getTags();
    }

    @JsonProperty("media_files")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<MediaFile> getMediaFiles() {
        return props.getMediaFiles();
    }

    @JsonProperty("revision_id")
    public long getPropertiesId() {
        return props.getId();
    }

    @JsonProperty("id")
    public long getActivityId() {
        return activity.getId();
    }

    @JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
    public static class RelatedActivityIdView {
        private final Long id;

        public RelatedActivityIdView(Long id) {
            this.id = id;
        }

        @JsonProperty("related_activity_id")
        public Long getId() {
            return id;
        }
    }
}
