package sk.cyrilgavala.reservationsApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserResponse user = userService.getByUsername(username);
		return mapToUserDetails(user);
	}

	private UserDetails mapToUserDetails(UserResponse userResponse) {
		return new User(
			userResponse.getUsername(),
			userResponse.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority(userResponse.getRole())));
	}
}
