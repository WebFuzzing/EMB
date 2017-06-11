package se.devscout.scoutapi.resource.v1;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Joiner;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Example;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.dao.ActivityDao;
import se.devscout.scoutapi.dao.ActivityRatingDao;
import se.devscout.scoutapi.model.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/v1/activities")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"activities"})
public class ActivityResourceV1 extends se.devscout.scoutapi.resource.ActivityResource {

    private static final Joiner COMMA_COINER = Joiner.on(',').skipNulls();

    public ActivityResourceV1(ActivityDao dao, ActivityRatingDao activityRatingDao) {
        super(dao, activityRatingDao);
    }

    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Search for activities")
    public Response all(@ApiParam(value = "The activity attributes to include in the response")//, example = "id,name,tags")
                        @QueryParam("attrs") String attrs,
                        @QueryParam("name") String name,

                        @ApiParam(value = "Words to look for in any text field. Prefix word with minus character to exclude activities with that word.")//, example = "mat -matematik")
                        @QueryParam("text") String text,

                        @ApiParam(value = "Show featured (hand-picked by editor) activities")
                        @QueryParam("featured") Boolean featured,

                        @ApiParam(value = "Show activites with at least one of the specified tags. Comma-separated list of category ids.")//, example = "12,34")
                        @QueryParam("categories") String tagIds,
                        @QueryParam("age_1") String age1,
                        @QueryParam("age_2") String age2,

                        @ApiParam(value = "Find activities suited for certain number of participants.")//, example = "1,5")
                        @QueryParam("participants") String numberOfParticipants,
                        @QueryParam("time_1") String time1,
                        @QueryParam("time_2") String time2,
                        @QueryParam("id") String activityIds,

                        @ApiParam(value = "Limit result to activities which the current user (as determined by API key) has marked as favourites.")
                        @QueryParam("my_favourites") Boolean myFavourites,

                        @ApiParam(value = "Show activities which have been rated by at least this many users")
                        @QueryParam("ratings_count_min") Long ratingsCountMin,

                        @ApiParam(value = "Show activities whose average rating is at this amount.")//, example = "1.0")
                        @QueryParam("ratings_average_min") Double ratingsAverageMin,

                        @ApiParam(value = "Limit result to a number of random activities matching the other conditions.")
                        @QueryParam("random") int random,

                        @ApiParam(value = "Limit result to the overall favourite activities. This means a list of activities sorted by the number of users who have marked them as their favourites. This parameter cannot be used together with any other filtering parameters (meaning that it is not possible to use it to, for example, show favourites for a particular category or age group).")
                        @QueryParam("favourites") int favourites) {
        if (myFavourites != null) {
            throw new WebApplicationException("API currently does not support filtering on your own favourites.");
        }
        return okResponse(getActivities(name,
                text,
                featured,
                tagIds,
                COMMA_COINER.join(age1, age2),
                numberOfParticipants,
                COMMA_COINER.join(time1, time2),
                activityIds,
                random,
                ratingsCountMin,
                ratingsAverageMin,
                null,
                favourites > 0 ? ActivityDao.SortOrder.favouritesCount : null,
                favourites > 0 ? favourites : null)
                .stream()
                .map(activity -> new ActivityApiV1View(activity))
                .collect(Collectors.toList()), attrs);
    }

    @Override
    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    @ApiOperation(value = "Delete an activity")
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        super.delete(authResult, response, id);
    }

    @Override
    @POST
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Create a new activity")
    public Activity create(@Auth @ApiParam(hidden = true) AuthResult authResult ,
                           @Context HttpServletResponse response, ActivityProperties properties){
        return super.create(authResult, response, properties);
    }

    @Override
    @GET
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(value = "Read a specific activity")
    public Response get(@PathParam("id") long id,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        return super.get(id, attrs);
    }

    @Override
    @GET
    @Timed
    @Path("{id}/rating")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(value = "Get the end-user's rating the an activity")
    public Response getRating(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id,

                              @ApiParam(value = API_DOCS_ATTRS_DESCR)
                              @QueryParam("attrs") String attrs) {
        return super.getRating(authResult, response, id, attrs);
    }

    @Override
    @POST
    @Timed
    @Path("{id}/rating")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(value = "Set the end-user's rating the an activity")
    public void postRating(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, ActivityRatingAttrs attrs) {
        super.postRating(authResult, response, id, attrs);
    }

    @Override
    @DELETE
    @Timed
    @Path("{id}/rating")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(value = "Remove the end-user's rating the an activity")
    public void deleteRating(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        super.deleteRating(authResult, response, id);
    }

    @Override
    @PUT
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(value = "Update an activity with new information. Activity properties not specified in the request will be cleared.")
    public Activity update(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, ActivityProperties properties) {
        return super.update(authResult, response, id, properties);
    }

    @Override
    @PATCH
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @ApiOperation(httpMethod = "PATCH", value = "Update an activity with new information. Only the properties specified in the request will be updated.")
    public Activity patch(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, ActivityProperties properties) {
        return super.patch(authResult, response, id, properties);
    }
}
