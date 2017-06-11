package se.devscout.scoutapi.resource;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import se.devscout.scoutapi.model.User;

@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class UserView {
    private String name;
    private long id;

    public UserView() {
    }

    public UserView(User user) {
        this.name = user.getName();
        this.id = user.getId();
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

}
