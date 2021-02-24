package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@Entity
@Table(name = "tag")
@NamedQueries(value = {
        @NamedQuery(name = "Tag.all", query = "SELECT t FROM Tag t"),
        @NamedQuery(name = "Tag.byGroupAndName", query = "" +
                "SELECT t " +
                "FROM Tag AS t " +
                "WHERE t.group LIKE :group AND t.name LIKE :name"),
        @NamedQuery(name = "Tag.byGroup", query = "" +
                "SELECT t " +
                "FROM Tag AS t " +
                "WHERE t.group LIKE :group"),
        @NamedQuery(name = "Tag.byName", query = "" +
                "SELECT t " +
                "FROM Tag AS t " +
                "WHERE t.name LIKE :name")
})
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(optional = false)
    @Column(name = "grp")
    private String group;

    @Basic(optional = false)
    private String name;

//    @ManyToMany(mappedBy="tags")
//    private List<Activity> activities;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "tag")
    @PrimaryKeyJoinColumn(name="tag_id")
    private TagDerived derived;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "media_file_id")
    private MediaFile mediaFile;


    public Tag() {
    }

    public Tag(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public Tag(long id) {
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Activity> getActivities() {
//        return activities;
//    }
//
//    public void setActivities(List<Activity> activities) {
//        this.activities = activities;
//    }
    /**
     * The purpose of this property is to provide the Activity entity with derived/calculated values for the activity's
     * rating. Derived properties could also have been implemented using the @Formula annotation, but that would cause
     * Hibernate to generate sub-selects instead of joins (which is the result of this @OneToOne mapping). Joins are
     * probably better for performance.
     */
    @JsonIgnore
    public TagDerived getDerived() {
        return derived;
    }

    public void setDerived(TagDerived derived) {
        this.derived = derived;
    }

    public long getActivitiesCount() {
        return derived != null && derived.getActivitiesCount() != null ? derived.getActivitiesCount() : 0;
    }

    /**
     * Used during deserialization only. Necessary for unit testing.
     */
    public void setActivitiesCount(Long count) {
        if (derived == null) {
            derived = new TagDerived();
        }
        derived.setActivitiesCount(count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(group, tag.group) &&
                Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, name);
    }

    @Override
    public String toString() {
        return group + '/' + name;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }
}
