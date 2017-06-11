package se.devscout.scoutapi.resource;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.auth.Role;
import se.devscout.scoutapi.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class UserProfileView {
    private List<String> rolePermissions;
    private String name;
    private Role role;

    public UserProfileView() {
    }

    public UserProfileView(User user) {
        name = user.getName();
        role = Stream.of(Role.values())
                .sorted((o1, o2) -> Integer.compare(o2.getLevel(), o1.getLevel()))
                .filter(role -> role.getLevel() <= user.getAuthorizationLevel())
                .findFirst()
                .orElse(Role.limited_user);
        rolePermissions = Stream.of(Permission.values()).filter(permission -> role.getLevel() >= permission.getLevel()).map(Enum::name).sorted().collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role.name();
    }

    public List<String> getRolePermissions() {
        return rolePermissions;
    }
}
