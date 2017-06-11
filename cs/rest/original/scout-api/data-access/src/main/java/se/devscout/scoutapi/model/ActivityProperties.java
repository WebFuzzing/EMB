package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "ACTIVITY_PROPERTIES")
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Size(max = 100)
    @Length(max = 100)
    protected String name;

    @Column(name = "date_published")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePublished;
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();
    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;
    @Basic
    @Column(name = "description_material")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionMaterial;
    @Basic
    @Column(name = "description_introduction")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionIntroduction;
    @Basic
    @Column(name = "description_prepare")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionPrepare;
    @Basic
    @Column(name = "description_main")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionMain;
    @Basic
    @Column(name = "description_safety")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionSafety;
    @Basic
    @Column(name = "description_notes")
    @Size(max = 20000)
    @Length(max = 20000)
    private String descriptionNotes;
    @Basic
    @Column(name = "age_min")
    @Max(100)
    private Integer ageMin;
    @Basic
    @Column(name = "age_max")
    @Max(100)
    private Integer ageMax;
    @Basic
    @Column(name = "participants_min")
    private Integer participantsMin;
    @Basic
    @Column(name = "participants_max")
    private Integer participantsMax;
    @Basic
    @Column(name = "time_min")
    private Integer timeMin;
    @Basic
    @Column(name = "time_max")
    private Integer timeMax;
    @Basic
    private Boolean featured;

    @Basic
    private String source;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "activityProperties")
    @XmlElement(name = "tag")
    @XmlElementWrapper(name = "tags")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<ActivityPropertiesTag> tags;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "activityProperties")
    @XmlElement(name = "mediaFile")
    @XmlElementWrapper(name = "mediaFiles")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<ActivityPropertiesMediaFile> mediaFiles;

    @ManyToOne
    private User author;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonBackReference
    private Activity activity;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "publishing_activity_id", unique = true, nullable = true)
    @JsonIgnore
    private Activity publishingActivity;

    public ActivityProperties() {
    }

    public ActivityProperties(String name) {
        this.name = name;
    }

    public ActivityProperties(ActivityProperties properties) {
        this.setName(properties.getName());
        this.setDescriptionIntroduction(properties.getDescriptionIntroduction());
        this.setDescriptionMain(properties.getDescriptionMain());
        this.setDescriptionMaterial(properties.getDescriptionMaterial());
        this.setDescriptionNotes(properties.getDescriptionNotes());
        this.setDescriptionPrepare(properties.getDescriptionPrepare());
        this.setDescriptionSafety(properties.getDescriptionSafety());
        this.setAgeMax(properties.getAgeMax());
        this.setAgeMin(properties.getAgeMin());
        this.setDateCreated(properties.getDateCreated());
        this.setDatePublished(properties.getDatePublished());
        this.setDateUpdated(properties.getDateUpdated());
        this.setParticipantsMax(properties.getParticipantsMax());
        this.setParticipantsMin(properties.getParticipantsMin());
        this.setTimeMax(properties.getTimeMax());
        this.setTimeMin(properties.getTimeMin());
        this.setFeatured(properties.isFeatured());
        this.setTags(properties.getTags());
        this.setMediaFiles(properties.getMediaFiles());
        this.setSource(properties.getSource());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getDescriptionMaterial() {
        return descriptionMaterial;
    }

    public void setDescriptionMaterial(String descriptionMaterial) {
        this.descriptionMaterial = descriptionMaterial;
    }

    public String getDescriptionIntroduction() {
        return descriptionIntroduction;
    }

    public void setDescriptionIntroduction(String descriptionIntroduction) {
        this.descriptionIntroduction = descriptionIntroduction;
    }

    public String getDescriptionPrepare() {
        return descriptionPrepare;
    }

    public void setDescriptionPrepare(String descriptionPrepare) {
        this.descriptionPrepare = descriptionPrepare;
    }

    public String getDescriptionMain() {
        return descriptionMain;
    }

    public void setDescriptionMain(String descriptionMain) {
        this.descriptionMain = descriptionMain;
    }

    public String getDescriptionSafety() {
        return descriptionSafety;
    }

    public void setDescriptionSafety(String descriptionSafety) {
        this.descriptionSafety = descriptionSafety;
    }

    public String getDescriptionNotes() {
        return descriptionNotes;
    }

    public void setDescriptionNotes(String descriptionNotes) {
        this.descriptionNotes = descriptionNotes;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin != null && ageMin != Integer.MIN_VALUE ? ageMin : null;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax != null && ageMax != Integer.MAX_VALUE ? ageMax : null;
    }

    public Integer getParticipantsMin() {
        return participantsMin;
    }

    public void setParticipantsMin(Integer participantsMin) {
        this.participantsMin = participantsMin != null && participantsMin != Integer.MIN_VALUE ? participantsMin : null;
    }

    public Integer getParticipantsMax() {
        return participantsMax;
    }

    public void setParticipantsMax(Integer participantsMax) {
        this.participantsMax = participantsMax != null && participantsMax != Integer.MAX_VALUE ? participantsMax : null;
    }

    public Integer getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(Integer timeMin) {
        this.timeMin = timeMin != null && timeMin != Integer.MIN_VALUE ? timeMin : null;
    }

    public Integer getTimeMax() {
        return timeMax;
    }

    public void setTimeMax(Integer timeMax) {
        this.timeMax = timeMax != null && timeMax != Integer.MAX_VALUE ? timeMax : null;
    }

    public Boolean isFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    // Use @JsonInclude to differentiate between "list is empty" and "list should be skipped"
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Tag> getTags() {
        return tags != null ? Collections.unmodifiableList(tags.stream().map(activityPropertiesTag -> activityPropertiesTag.getTag()).collect(Collectors.toList())) : null;
    }

    public void setTags(Collection<Tag> tags) {
        if (tags != null) {
            this.tags = new ArrayList<>();
            this.tags.addAll(tags.stream().map(tag -> new ActivityPropertiesTag(this, tag)).collect(Collectors.toList()));
        } else {
            this.tags = null;
        }
    }

    // Use @JsonInclude to differentiate between "list is empty" and "list should be skipped"
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<MediaFile> getMediaFiles() {
        return mediaFiles != null ? Collections.unmodifiableList(mediaFiles.stream().map(activityPropertiesMediaFile -> activityPropertiesMediaFile.getMediaFile()).collect(Collectors.toList())) : null;
    }

    public void setMediaFiles(Collection<MediaFile> mediaFiles) {
        if (mediaFiles != null) {
            this.mediaFiles = new ArrayList<>();
            this.mediaFiles.addAll(mediaFiles.stream().map(mediaFile -> new ActivityPropertiesMediaFile(this, mediaFile)).collect(Collectors.toList()));
        } else {
            this.mediaFiles = null;
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Activity getPublishingActivity() {
        return publishingActivity;
    }

    public void setPublishingActivity(Activity publishingActivity) {
        if (activity != null && publishingActivity != null && activity.getId() != publishingActivity.getId()) {
            throw new IllegalArgumentException("Cannot make properties object the published properties of activity " + publishingActivity.getId() + " while being owned by activity" + activity.getId());
        }
        this.publishingActivity = publishingActivity;
    }

    public boolean isContentEqual(ActivityProperties that) {
        if (this == that) return true;
        boolean equal = Objects.equals(name, that.name) &&
//                Objects.equals(datePublished, that.datePublished) &&
//                Objects.equals(dateCreated, that.dateCreated) &&
//                Objects.equals(dateUpdated, that.dateUpdated) &&
                Objects.equals(descriptionMaterial, that.descriptionMaterial) &&
                Objects.equals(descriptionIntroduction, that.descriptionIntroduction) &&
                Objects.equals(descriptionPrepare, that.descriptionPrepare) &&
                Objects.equals(descriptionMain, that.descriptionMain) &&
                Objects.equals(descriptionSafety, that.descriptionSafety) &&
                Objects.equals(descriptionNotes, that.descriptionNotes) &&
                Objects.equals(ageMin, that.ageMin) &&
                Objects.equals(ageMax, that.ageMax) &&
                Objects.equals(participantsMin, that.participantsMin) &&
                Objects.equals(participantsMax, that.participantsMax) &&
                Objects.equals(timeMin, that.timeMin) &&
                Objects.equals(timeMax, that.timeMax) &&
                Objects.equals(featured, that.featured) &&
                Objects.equals(source, that.source) &&
//                Objects.equals(tags, that.tags) &&
//                Objects.equals(mediaFiles, that.mediaFiles) &&
                Objects.equals(author, that.author) &&
//                Objects.equals(activity, that.activity) &&
//                Objects.equals(publishingActivity, that.publishingActivity) &&
                Objects.nonNull(tags) && getTagList(tags).equals(getTagList(that.tags)) &&
                Objects.nonNull(mediaFiles) && getMediaFiles(mediaFiles).equals(getMediaFiles(that.mediaFiles));
        return equal;
    }

    private List<String> getTagList(List<ActivityPropertiesTag> tags) {
        return tags.stream().map(apt -> apt.getTag().getGroup() + ";" + apt.getTag().getName()).sorted().collect(Collectors.toList());
    }

    private List<String> getMediaFiles(List<ActivityPropertiesMediaFile> tags) {
        return tags.stream().map(apt -> apt.getMediaFile().getUri()).sorted().collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, datePublished, dateCreated, dateUpdated, descriptionMaterial, descriptionIntroduction, descriptionPrepare, descriptionMain, descriptionSafety, descriptionNotes, ageMin, ageMax, participantsMin, participantsMax, timeMin, timeMax, featured, source, tags, mediaFiles, author, activity, publishingActivity);
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' + ", id=" + id;
    }
}
