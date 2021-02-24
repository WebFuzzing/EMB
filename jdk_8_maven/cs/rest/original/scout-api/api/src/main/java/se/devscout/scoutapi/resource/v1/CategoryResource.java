package se.devscout.scoutapi.resource.v1;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.dao.TagDao;
import se.devscout.scoutapi.model.Tag;
import se.devscout.scoutapi.resource.TagResource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/categories")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"tags or categories"})
public class CategoryResource extends TagResource {

    public CategoryResource(TagDao dao) {
        super(dao);
    }

    @Override
    @GET
    @Timed
    @UnitOfWork
    public Response all(@ApiParam(value = "Returned tags must have this text in their group names.")
                        @QueryParam("group") String group,

                        @ApiParam(value = "Returned tags must have this text in their names.")
                        @QueryParam("name") String name,

                        @ApiParam(value = "Returned tags must have this text in their names.")
                        @QueryParam("min_activities_count") Long minActivitiesCount,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        return super.all(group, name, minActivitiesCount, attrs);
    }

    @Override
    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, @PathParam("id") long id) {
        super.delete(authResult, response, id);
    }

    @Override
    @POST
    @Timed
    @UnitOfWork
    public Tag create(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, Tag tag) {
        return super.create(authResult, response, tag);
    }

    @Override
    @GET
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response get(@PathParam("id") long id,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        return super.get(id, attrs);
    }

    @Override
    @PUT
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Tag update(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, @PathParam("id") long id, Tag updatedTag) {
        return super.update(authResult, response, id, updatedTag);
    }
}
