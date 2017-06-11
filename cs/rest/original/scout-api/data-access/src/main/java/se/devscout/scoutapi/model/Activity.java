package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ACTIVITY")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries(value = {
        @NamedQuery(name = "Activity.all", query = "SELECT a FROM Activity a"),
        @NamedQuery(name = "ActivityRelation.deleteForActivity", query = "DELETE FROM ActivityRelation ar WHERE ar.activity1 = :activity OR ar.activity2 = :activity"),
        @NamedQuery(name = "ActivityRelation.relatedActivityIds", query = "SELECT ar.activity2.id FROM ActivityRelation ar WHERE ar.activity1.id = :sourceId")
})
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activity", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @XmlElement(name = "properties")
    @XmlElementWrapper(name = "propertiesRevisions")
    @BatchSize(size = 50)
    private List<ActivityProperties> propertiesRevisions = new ArrayList<ActivityProperties>();

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "publishingActivity", cascade = CascadeType.ALL)
    @JsonProperty("properties")
    private ActivityProperties publishedProperties;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "activity")
    @PrimaryKeyJoinColumn(name="activity_id")
    private ActivityDerived derived;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "activity1")
    @XmlElement(name = "relatedActivity")
    @XmlElementWrapper(name = "relatedActivities")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<ActivityRelation> relations;

    /**
     * The purpose of this property is to provide the Activity entity with derived/calculated values for the activity's
     * rating. Derived properties could also have been implemented using the @Formula annotation, but that would cause
     * Hibernate to generate sub-selects instead of joins (which is the result of this @OneToOne mapping). Joins are
     * probably better for performance.
     */
    @JsonIgnore
    public ActivityDerived getDerived() {
        return derived;
    }

    public void setDerived(ActivityDerived derived) {
        this.derived = derived;
    }

    public Activity() {
        this(0, new ActivityProperties());
    }

    public Activity(String name) {
        this(0, new ActivityProperties(name));
    }

    public Activity(ActivityProperties initialProperties) {
        this(0, initialProperties);
    }

    public Activity(int id, ActivityProperties initialProperties) {
        this.id = id;
        addAndPublish(initialProperties);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void updateProperties(ActivityProperties newValues, boolean patch) {
        ActivityProperties properties = patch ? new ActivityProperties(publishedProperties) : new ActivityProperties();

        if (newValues.getName() != null) {
            properties.setName(newValues.getName());
        }
        if (newValues.getSource() != null) {
            properties.setSource(newValues.getSource());
        }
        if (newValues.getAuthor() != null) {
            properties.setAuthor(newValues.getAuthor());
        }
        if (newValues.getDescriptionIntroduction() != null) {
            properties.setDescriptionIntroduction(newValues.getDescriptionIntroduction());
        }
        if (newValues.getDescriptionMain() != null) {
            properties.setDescriptionMain(newValues.getDescriptionMain());
        }
        if (newValues.getDescriptionMaterial() != null) {
            properties.setDescriptionMaterial(newValues.getDescriptionMaterial());
        }
        if (newValues.getDescriptionNotes() != null) {
            properties.setDescriptionNotes(newValues.getDescriptionNotes());
        }
        if (newValues.getDescriptionPrepare() != null) {
            properties.setDescriptionPrepare(newValues.getDescriptionPrepare());
        }
        if (newValues.getDescriptionSafety() != null) {
            properties.setDescriptionSafety(newValues.getDescriptionSafety());
        }
        if (newValues.getAgeMax() != null) {
            properties.setAgeMax(newValues.getAgeMax());
        }
        if (newValues.getAgeMin() != null) {
            properties.setAgeMin(newValues.getAgeMin());
        }
        if (newValues.getDateCreated() != null) {
            properties.setDateCreated(newValues.getDateCreated());
        }
        if (newValues.getDatePublished() != null) {
            properties.setDatePublished(newValues.getDatePublished());
        }
        if (newValues.getDateUpdated() != null) {
            properties.setDateUpdated(newValues.getDateUpdated());
        }
        if (newValues.getParticipantsMax() != null) {
            properties.setParticipantsMax(newValues.getParticipantsMax());
        }
        if (newValues.getParticipantsMin() != null) {
            properties.setParticipantsMin(newValues.getParticipantsMin());
        }
        if (newValues.getTimeMax() != null) {
            properties.setTimeMax(newValues.getTimeMax());
        }
        if (newValues.getTimeMin() != null) {
            properties.setTimeMin(newValues.getTimeMin());
        }
        if (newValues.isFeatured() != null) {
            properties.setFeatured(newValues.isFeatured());
        }
        if (newValues.getTags() != null) {
            properties.setTags(newValues.getTags());
        }
        if (newValues.getTags() != null) {
            properties.setMediaFiles(newValues.getMediaFiles());
        }

        addAndPublish(properties);
    }

    private void addAndPublish(ActivityProperties properties) {
        properties.setActivity(this);
        propertiesRevisions.add(properties);
        setPublishedProperties(properties);
    }

    public List<ActivityProperties> getPropertiesRevisions() {
        return propertiesRevisions;
    }

    public ActivityProperties getProperties() {
        return publishedProperties;
    }

    public void setPublishedProperties(ActivityProperties publishedProperties) {
        if (publishedProperties != null) {
            publishedProperties.setPublishingActivity(null);
        }
        this.publishedProperties = publishedProperties;
        this.publishedProperties.setPublishingActivity(this);
    }

    public long getRatingsCount() {
        return derived != null && derived.getRatingsCount() != null ? derived.getRatingsCount() : 0;
    }

    public long getRatingsSum() {
        return derived != null && derived.getRatingsSum() != null ? derived.getRatingsSum() : 0;
    }

    public long getFavouritesCount() {
        return derived != null && derived.getFavouritesCount() != null ? derived.getFavouritesCount() : 0;
    }

    /**
     * @return the average rating, or -1 if activity has not yet been rated by anyone.
     */
    public double getRatingsAverage() {
        return getRatingsCount() > 0 ? 1.0 * getRatingsSum() / getRatingsCount() : -1;
    }

    /**
     * Used during deserialization only. Necessary for unit testing.
     */
    public void setRatingsCount(Long count) {
        if (derived == null) {
            derived = new ActivityDerived();
        }
        derived.setRatingsCount(count);
    }

    /**
     * Used during deserialization only. Necessary for unit testing.
     */
    public void setRatingsSum(Long sum) {
        if (derived == null) {
            derived = new ActivityDerived();
        }
        derived.setRatingsSum(sum);
    }

    /**
     * Used during deserialization only. Necessary for unit testing.
     */
    public void setFavouritesCount(Long count) {
        if (derived == null) {
            derived = new ActivityDerived();
        }
        derived.setFavouritesCount(count);
    }

    /**
     * Used during deserialization only. Necessary for unit testing.
     */
    public void setRatingsAverage(long avg) {
        // Intentionally empty.
    }

    // Use @JsonInclude to differentiate between "list is empty" and "list should be skipped"
    @JsonIgnore
    public List<Activity> getRelated() {
        return relations != null ? Collections.unmodifiableList(relations.stream().map(relation -> relation.getActivity2()).collect(Collectors.toList())) : null;
    }

    @JsonIgnore
    public List<ActivityRelation> getRelations() {
        return relations;
    }


    // Use @JsonInclude to differentiate between "list is empty" and "list should be skipped"
    @JsonProperty("related")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long[] getRelatedIds() {
        return relations != null ? relations.stream().map(relation -> Long.valueOf(relation.getActivity2Id())).toArray(value -> new Long[value]) : null;
    }
}
