package se.devscout.scoutapi.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ActivityRatingAttrs {
    @Basic
    protected Integer rating;

    @Basic(optional = false)
    protected Boolean favourite;

    public ActivityRatingAttrs() {
    }

    public ActivityRatingAttrs(Integer rating) {
        this.rating = rating;
    }

    public ActivityRatingAttrs(Integer rating, Boolean favourite) {
        this.rating = rating;
        this.favourite = favourite;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
