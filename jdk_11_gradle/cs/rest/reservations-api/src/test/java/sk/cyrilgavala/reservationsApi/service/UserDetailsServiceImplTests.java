package sk.cyrilgavala.reservationsApi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link UserDetailsServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

	@Mock
	private UserService userService;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	@Test
	void loadUserByUsername_userFound_pass() {
		when(userService.getByUsername("userName")).thenReturn(new UserResponse("userId", "userName", "user@test.com", "userPassword", "USER"));
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername("userName");
			assertNotNull(userDetails);
			assertEquals("userName", userDetails.getUsername());
			assertEquals("userPassword", userDetails.getPassword());
			assertThat(userDetails.getAuthorities()).hasSize(1).isEqualTo(Collections.unmodifiableSet(Set.of(new SimpleGrantedAuthority("USER"))));
		} catch (UsernameNotFoundException ex) {
			fail("Error should not be thrown");
		}
	}

	@Test
	void loadUserByUsername_userNotFound_errorThrown() {
		when(userService.getByUsername("userName")).thenThrow(new UsernameNotFoundException("User not found with username: userName"));
		try {
			userDetailsService.loadUserByUsername("userName");
			fail("Error should be thrown");
		} catch (UsernameNotFoundException ex) {
			assertEquals("User not found with username: userName", ex.getMessage());
		}
	}
}
