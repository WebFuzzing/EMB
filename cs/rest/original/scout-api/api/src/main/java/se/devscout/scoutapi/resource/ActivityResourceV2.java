package se.devscout.scoutapi.resource;

import com.codahale.metrics.annotation.Timed;
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
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.ActivityRatingAttrs;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/activities")
@Api(tags = {"activities"})
public class ActivityResourceV2 extends ActivityResource {
    public ActivityResourceV2(ActivityDao dao, ActivityRatingDao activityRatingDao) {
        super(dao, activityRatingDao);
    }

    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Search for activities")
    public Response all(@ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs,

                        @ApiParam(value = "Words to look for in activity names. Prefix word with minus character to exclude activities with that word.")//, example = "mat -matematik")
                        @QueryParam("name") String name,

                        @ApiParam(value = "Words to look for in any text field. Prefix word with minus character to exclude activities with that word.")//, example = "mat -matematik")
                        @QueryParam("text") String text,

                        @ApiParam(value = "Show featured (hand-picked by editor) activities")
                        @QueryParam("featured") Boolean featured,

                        @ApiParam(value = "Show activites with at least one of the specified tags. Comma-separated list of tag ids.")//, example = "12,34")
                        @QueryParam("categories") String tagIds,

                        @ApiParam(value = "Find activities suited for certain ages. Comma-separated list of ages.")//, example = "8,12")
                        @QueryParam("ages") String ages,

                        @ApiParam(value = "Find activities suited for certain number of participants.")//, example = "1,5")
                        @QueryParam("participants") String numberOfParticipants,

                        @ApiParam(value = "Find activities which can be completed in a certain amount of time. Unit: minutes.")//, example = "15")
                        @QueryParam("durations") String durations,

                        @ApiParam(value = "Find specific activites based on their internal identifiers. It is not expected that end-users know these number. Comma-separated list.")
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
        return okResponse(getActivities(
                        name,
                        text,
                        featured,
                        tagIds,
                        ages,
                        numberOfParticipants,
                        durations,
                        activityIds,
                        random,
                        ratingsCountMin,
                        ratingsAverageMin,
                        null,
                        favourites > 0 ? ActivityDao.SortOrder.favouritesCount : null,
                        favourites > 0 ? favourites : null),
                attrs);
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
