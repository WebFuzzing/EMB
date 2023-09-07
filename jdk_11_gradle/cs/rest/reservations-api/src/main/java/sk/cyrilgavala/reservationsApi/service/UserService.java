package sk.cyrilgavala.reservationsApi.service;

import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

public interface UserService {

	UserResponse getByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	UserResponse saveUser(User user);
}
