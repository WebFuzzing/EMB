package sk.cyrilgavala.reservationsApi.mapper;

import org.mapstruct.Mapper;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserResponse modelToResponse(User model);

}
