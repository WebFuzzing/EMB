package se.devscout.scoutapi.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.UserDao;
import se.devscout.scoutapi.model.User;
import se.devscout.scoutapi.model.UserIdentity;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;

@Path("/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"users"})
public class UserResource extends AbstractResource {

    private final UserDao dao;

    public UserResource(UserDao dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    @UnitOfWork
    public Response all(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @QueryParam("name") String name,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        doAuth(authResult, response, null);
        List<User> users = Strings.isNullOrEmpty(name) ? dao.all() : dao.byName(name);
        return okResponse(users, attrs);
    }

    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        doAuth(authResult, response, Permission.auth_user_edit);
        dao.delete(dao.readUser(id));
    }

    @POST
    @Timed
    @UnitOfWork
    public User create(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, User user) {
        doAuth(authResult, response, Permission.auth_user_create);
        assertAuthorizationLevel(authResult.getUser(), user);
        return dao.create(user);
    }

    @GET
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response get(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        doAuth(authResult, response, Permission.auth_user_edit);
        User user = dao.readUser(id);
        return okResponse(user, attrs);
    }

    @GET
    @Timed
    @Path("profile")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public UserProfileView profile(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        doAuth(authResult, response, null);
        return new UserProfileView(authResult.getUser());
    }

    @PUT
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public User update(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, User updatedUser) {
        doAuth(authResult, response, Permission.auth_user_edit);
        assertAuthorizationLevel(authResult.getUser(), updatedUser);

        User persisted = dao.readUser(id);

        persisted.setName(updatedUser.getName());

        Iterator<UserIdentity> iterator = persisted.getIdentities().iterator();
        while (iterator.hasNext()) {
            UserIdentity persistedItem = iterator.next();
            UserIdentity updatedItem = updatedUser.getIdentityById(persistedItem.getId());
            if (updatedItem != null) {
                // Existing user item has been UPDATED
                persistedItem.setType(updatedItem.getType());
                persistedItem.setValue(updatedItem.getValue());
            } else {
                // Existing user item has been DELETED
                iterator.remove();
            }
        }
        for (UserIdentity updatedItem : updatedUser.getIdentities()) {
            if (updatedItem.getId() < 1) {
                // New user item has been CREATED
                persisted.addIdentity(updatedItem.getType(), updatedItem.getValue());
            }
        }
        dao.update(persisted);
        return persisted;
    }

    private void assertAuthorizationLevel(User grantor, User grantee) {
        if (grantor.getAuthorizationLevel() < grantee.getAuthorizationLevel()) {
            throw new WebApplicationException("Cannot set authorization level to higher than your own.", HttpURLConnection.HTTP_FORBIDDEN);
        }
    }
}
