package se.devscout.scoutapi.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.SystemMessageDao;
import se.devscout.scoutapi.model.SystemMessage;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v1/system_messages")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"system messages"})
public class SystemMessageResource extends AbstractResource {

    private final SystemMessageDao dao;

    public SystemMessageResource(SystemMessageDao dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    @UnitOfWork
    public Response all(@QueryParam("key") String key, @QueryParam("valid") ValidityInterval validityInterval,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        List<SystemMessage> messages;
        if (validityInterval != null) {
            switch (validityInterval) {
                case now:
                    messages = dao.find(Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0))), false);
                    break;
                case now_and_future:
                    messages = dao.find(Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0))), true);
                    break;
                default:
                    messages = dao.all();
                    break;
            }
        } else {
            messages = dao.all();
        }
        List<SystemMessage> filteredMessages = messages.stream().filter(systemMessage -> Strings.isNullOrEmpty(key) ? true : Splitter.on(',').splitToList(key).stream().anyMatch(k -> systemMessage.getKey().startsWith(k))).collect(Collectors.toList());
        return okResponse(filteredMessages, attrs);
    }

    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id) {
        doAuth(authResult, response, Permission.system_message_manage);
        dao.delete(dao.read(id));
    }

    @POST
    @Timed
    @UnitOfWork
    public SystemMessage create(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, SystemMessage systemMessage) {
        doAuth(authResult, response, Permission.system_message_manage);
        return dao.create(systemMessage);
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
    public SystemMessage update(@Auth @ApiParam(hidden = true) AuthResult authResult , @Context HttpServletResponse response, @PathParam("id") long id, SystemMessage updatedSystemMessage) {
        doAuth(authResult, response, Permission.system_message_manage);
        SystemMessage persisted = dao.read(id);

        persisted.setValue(updatedSystemMessage.getValue());
        persisted.setKey(updatedSystemMessage.getKey());
        persisted.setValidFrom(updatedSystemMessage.getValidFrom());
        persisted.setValidTo(updatedSystemMessage.getValidTo());

        dao.update(persisted);
        return persisted;
    }
}
