package se.devscout.scoutapi.resource;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.Api;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.auth.Role;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/v1/system")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"system status"})
public class SystemResource extends AbstractResource {
    @GET
    @Path("ping")
    public Response ping() {
        return Response.noContent().build();
    }

    @GET
    @Path("roles")
    public RolesView roles() {
        return new RolesView(
                Stream.of(Permission.values()).collect(Collectors.toMap(Enum::name, Permission::getLevel)),
                Stream.of(Role.values()).collect(Collectors.toMap(Enum::name, Role::getLevel))
        );
    }

    @JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
    public static class RolesView {
        private Map<String, Integer> permissionLevels;
        private Map<String, Integer> roleLevels;

        public RolesView() {
        }

        public RolesView(Map<String, Integer> permissionLevels, Map<String, Integer> roleLevels) {
            this.permissionLevels = permissionLevels;
            this.roleLevels = roleLevels;
        }

        public Map<String, Integer> getPermissionLevels() {
            return permissionLevels;
        }

        public Map<String, Integer> getRoleLevels() {
            return roleLevels;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolesView)) return false;
            RolesView rolesView = (RolesView) o;
            return Objects.equals(permissionLevels, rolesView.permissionLevels) &&
                    Objects.equals(roleLevels, rolesView.roleLevels);
        }

        @Override
        public int hashCode() {
            return Objects.hash(permissionLevels, roleLevels);
        }
    }
}
