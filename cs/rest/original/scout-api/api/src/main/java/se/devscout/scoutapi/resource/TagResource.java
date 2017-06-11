package se.devscout.scoutapi.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.TagDao;
import se.devscout.scoutapi.model.Tag;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/tags")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"tags or categories"})
public class TagResource extends AbstractResource {

    private final TagDao dao;

    public TagResource(TagDao dao) {
        this.dao = dao;
    }

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
        return okResponse(dao.find(group, name, minActivitiesCount), attrs);
    }

    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, @PathParam("id") long id) {
        doAuth(authResult, response, Permission.category_edit);
        dao.delete(dao.read(id));
    }

    @POST
    @Timed
    @UnitOfWork
    public Tag create(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, Tag tag) {
        doAuth(authResult, response, Permission.category_create);
        return dao.create(tag);
    }

    @GET
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response get(@PathParam("id") long id,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        return okResponse(dao.read(id), attrs);
    }

    @PUT
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Tag update(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, @PathParam("id") long id, Tag updatedTag) {
        doAuth(authResult, response, Permission.category_edit);
        Tag persisted = dao.read(id);

        persisted.setName(updatedTag.getName());
        persisted.setGroup(updatedTag.getGroup());

        dao.update(persisted);
        return persisted;
    }
}
