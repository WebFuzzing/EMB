package se.devscout.scoutapi.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.dropwizard.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.ScoutAPIApplication;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.auth.apikey.ApiKeyAuthenticator;
import se.devscout.scoutapi.model.IdentityType;
import se.devscout.scoutapi.model.UserIdentity;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;

@Produces(MediaType.APPLICATION_JSON)
public class AbstractResource {

    private static final String HTTP_RESPONSE_HEADER_API_KEY = "X-ScoutAdmin-APIKey";
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    private static final ObjectWriter DEFAULT_WRITER = OBJECT_MAPPER.writer(ScoutAPIApplication.DEFAULT_FILTER_PROVIDER);
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);
    protected static final String API_DOCS_ATTRS_DESCR = "The attributes to include in the response. Comma-separated list.";

    protected void doAuth(AuthResult authResult, HttpServletResponse response, Permission requiredAuthorizationLevel) {
        if (authResult != null && response != null) {
            if (!ApiKeyAuthenticator.ID.equals(authResult.getAuthenticator())) {
                for (UserIdentity identity : authResult.getUser().getIdentities()) {
                    if (identity.getType() == IdentityType.API) {
                        response.setHeader(HTTP_RESPONSE_HEADER_API_KEY, identity.getValue());
                    }
                }
            }

            if (requiredAuthorizationLevel != null && !requiredAuthorizationLevel.isGrantedTo(authResult.getUser())) {
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
        }
    }

    protected String toJson(Object o, String properties) {
        return toJson(o, Strings.isNullOrEmpty(properties) ? null : Sets.newHashSet(Splitter.on(',').split(properties)));
    }

    private String toJson(Object o, HashSet<String> properties) {
        ObjectWriter writer = properties != null ? OBJECT_MAPPER.writer(new SimpleFilterProvider().addFilter("custom", SimpleBeanPropertyFilter.filterOutAllExcept(properties))) : DEFAULT_WRITER;
        try {
            return writer.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new WebApplicationException("Could not generated JSON from " + o.getClass().getSimpleName() + " object.", e);
        }
    }

    protected Response okResponse(Object entity, String includedEntityProperties) {
        String json = toJson(entity, includedEntityProperties);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("JSON returned to client: " + json);

        return Response.ok().entity(json).build();
    }
}
