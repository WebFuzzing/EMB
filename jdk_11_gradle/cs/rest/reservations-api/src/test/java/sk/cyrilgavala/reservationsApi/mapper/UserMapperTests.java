package sk.cyrilgavala.reservationsApi.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test cases for {@link UserMapper}.
 */
@ExtendWith(MockitoExtension.class)
public class UserMapperTests {

	private final UserMapper mapper = new UserMapperImpl();

	@Test
	void modelToResponse_validModel() {
		User user = new User("userId", "userName", "user@test.com", "userPassword", "USER");
		UserResponse response = mapper.modelToResponse(user);
		assertNotNull(response);
		assertEquals("userId", response.getId());
		assertEquals("userName", response.getUsername());
		assertEquals("user@test.com", response.getEmail());
		assertEquals("userPassword", response.getPassword());
		assertEquals("USER", response.getRole());
	}

	@Test
	void modelToResponse_null() {
		UserResponse response = mapper.modelToResponse(null);
		assertNull(response);
	}
}
