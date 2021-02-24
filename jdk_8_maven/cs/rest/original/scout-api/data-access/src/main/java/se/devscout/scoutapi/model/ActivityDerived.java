package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * This is a fake entity in the sense that it actually maps to a view instead of a table.
 * <p>
 * The purpose is to provide the Activity entity with derived/calculated values for the activity's rating. Derived
 * properties could also be implemented using the @Formula annotation, but that would cause Hibernate to generate
 * sub-selects instead of joins (which is the result of this @OneToOne mapping). Joins are probably better for performance.
 */
@Entity
@Table(name = "ACTIVITY_DERIVED")
@Immutable
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityDerived {
    @Id
    @Column(name = "activity_id")
    private long activityId;

    @OneToOne
    @MapsId(value = "activityId")
    private Activity activity;

    @Column(name = "favourites_count")
    private Long favouritesCount;

    @Column(name = "ratings_count")
    private Long ratingsCount;

    @Column(name = "ratings_sum")
    private Long ratingsSum;

    @Column(name = "ratings_avg")
    private Double ratingsAverage;

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public Long getRatingsSum() {
        return ratingsSum;
    }

    public void setRatingsCount(Long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setRatingsSum(Long ratingsSum) {
        this.ratingsSum = ratingsSum;
    }

    public Long getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(Long favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Double getRatingsAverage() {
        return ratingsAverage;
    }

    public void setRatingsAverage(Double ratingsAverage) {
        this.ratingsAverage = ratingsAverage;
    }
}
