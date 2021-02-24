package se.devscout.scoutapi.resource.v1;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.ActivityDao;
import se.devscout.scoutapi.dao.ActivityRatingDao;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.ActivityRating;
import se.devscout.scoutapi.model.ActivityRatingAttrs;
import se.devscout.scoutapi.resource.AbstractResource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v1/favourites")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"user content"})
public class FavouritesResourceV1 extends AbstractResource {

    private final ActivityRatingDao activityRatingDao;
    private final ActivityDao activityDao;

    public FavouritesResourceV1(ActivityRatingDao activityRatingDao, ActivityDao activityDao) {
        this.activityRatingDao = activityRatingDao;
        this.activityDao = activityDao;
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Long> get(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        doAuth(authResult, response, Permission.rating_set_own);
        return activityRatingDao.all(authResult.getUser()).stream().filter(ar -> ar.isFavourite() != null && ar.isFavourite()).map(activityRating -> activityRating.getActivity().getId()).collect(Collectors.toList());
    }

    @POST
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public void put(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, PutFavouritesEntity properties) {
        doAuth(authResult, response, Permission.rating_set_own);
        activityRatingDao.all(authResult.getUser()).forEach(activityRating -> {
            boolean shouldBeSetAsFavourite = properties.getIdList().stream().anyMatch(aLong -> aLong == activityRating.getActivity().getId());
            boolean isFavourite = activityRating.isFavourite() != null && activityRating.isFavourite().booleanValue();
            if (shouldBeSetAsFavourite != isFavourite) {
                activityRating.setFavourite(shouldBeSetAsFavourite);
                activityRatingDao.update(activityRating);
            }
            properties.getIdList().remove(activityRating.getActivity().getId());
        });
        properties.getIdList().forEach(aLong -> {
            Activity activity = activityDao.read(aLong);
            if (activity != null) {
                activityRatingDao.create(new ActivityRating(activity, authResult.getUser(), null, true));
            }
        });
    }

    public static class PutFavouritesEntity {
        private List<Long> id;

        public PutFavouritesEntity() {
        }

        public PutFavouritesEntity(List<Long> id) {
            this.id = id;
        }

        @JsonProperty("id")
        public List<Long> getIdList() {
            return id;
        }

        public void setId(List<Long> id) {
            this.id = id;
        }
    }
}
