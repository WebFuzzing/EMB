package sk.cyrilgavala.reservationsApi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sk.cyrilgavala.reservationsApi.mapper.UserMapper;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.repository.UserRepository;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

	private static final String ID = "userId";
	private static final String USERNAME = "username";
	private static final String EMAIL = "user@test.com";
	private static final String PASSWORD = "userPassword";
	private static final String ROLE = "USER";

	@Mock
	private UserRepository repository;
	@Mock
	private UserMapper mapper;
	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void getByUsername_userFound_pass() {
		User user = new User(ID, USERNAME, EMAIL, PASSWORD, ROLE);
		UserResponse userResponse = new UserResponse(ID, USERNAME, EMAIL, PASSWORD, ROLE);
		when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
		when(mapper.modelToResponse(user)).thenReturn(userResponse);

		try {
			UserResponse response = userService.getByUsername(USERNAME);
			assertNotNull(response);
			assertEquals(ID, response.getId());
			assertEquals(USERNAME, response.getUsername());
			assertEquals(EMAIL, response.getEmail());
			assertEquals(PASSWORD, response.getPassword());
			assertEquals(ROLE, response.getRole());
		} catch (UsernameNotFoundException ex) {
			fail("Error should not be thrown");
		}
	}

	@Test
	void getByUsername_userNotFound_errorThrown() {
		when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

		try {
			userService.getByUsername(USERNAME);
			fail("Error should not be thrown");
		} catch (UsernameNotFoundException ex) {
			assertEquals("User not found with username: " + USERNAME, ex.getMessage());
		}
	}

	@Test
	void existsByUsername() {
		when(repository.existsByUsername(USERNAME)).thenReturn(true);
		assertTrue(userService.existsByUsername(USERNAME));
	}

	@Test
	void existsByEmail() {
		when(repository.existsByEmail(EMAIL)).thenReturn(true);
		assertTrue(userService.existsByEmail(EMAIL));
	}

	@Test
	void saveUser() {
		User user = new User(ID, USERNAME, EMAIL, PASSWORD, ROLE);
		UserResponse userResponse = new UserResponse(ID, USERNAME, EMAIL, PASSWORD, ROLE);
		when(repository.insert(user)).thenReturn(user);
		when(mapper.modelToResponse(user)).thenReturn(userResponse);

		UserResponse response = userService.saveUser(user);
		assertNotNull(response);
		assertEquals(ID, response.getId());
		assertEquals(USERNAME, response.getUsername());
		assertEquals(EMAIL, response.getEmail());
		assertEquals(PASSWORD, response.getPassword());
		assertEquals(ROLE, response.getRole());
	}
}
