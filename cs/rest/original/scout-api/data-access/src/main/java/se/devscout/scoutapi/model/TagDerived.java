package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * This is a fake entity in the sense that it actually maps to a view instead of a table.
 * <p>
 * The purpose is to provide the Activity entity with derived/calculated values for the tag's rating. Derived
 * properties could also be implemented using the @Formula annotation, but that would cause Hibernate to generate
 * sub-selects instead of joins (which is the result of this @OneToOne mapping). Joins are probably better for performance.
 */
@Entity
@Table(name = "TAG_DERIVED")
@Immutable
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class TagDerived {
    @Id
    @Column(name = "tag_id")
    private long tagId;

    @OneToOne
    @MapsId(value = "tagId")
    private Tag tag;

    @Column(name = "activities_count")
    private Long activitiesCount;

    public Long getActivitiesCount() {
        return activitiesCount;
    }

    public void setActivitiesCount(Long activitiesCount) {
        this.activitiesCount = activitiesCount;
    }
}
