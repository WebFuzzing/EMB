package sk.cyrilgavala.reservationsApi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sk.cyrilgavala.reservationsApi.config.SecurityConfiguration;
import sk.cyrilgavala.reservationsApi.exception.DuplicateUserException;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.security.TokenProvider;
import sk.cyrilgavala.reservationsApi.service.UserService;
import sk.cyrilgavala.reservationsApi.web.request.LoginRequest;
import sk.cyrilgavala.reservationsApi.web.request.RegisterRequest;
import sk.cyrilgavala.reservationsApi.web.response.AuthResponse;

import javax.validation.Valid;
import java.util.Base64;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserRestController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	@Operation(summary = "Perform user's login to retrieve access token.", description = "Perform user's login to retrieve access token.")
	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
		String token = authenticateAndGetToken(loginRequest.getUsername(), new String(Base64.getDecoder().decode(loginRequest.getPassword())));
		return new AuthResponse(token);
	}

	@Operation(summary = "Perform user's registration to save a user and retrieve access token.", description = "Perform user's registration to save a user and retrieve access token.")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/register")
	public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
		if (userService.existsByUsername(registerRequest.getUsername())) {
			throw new DuplicateUserException(String.format("Username %s already been used", registerRequest.getUsername()));
		}
		if (userService.existsByEmail(registerRequest.getEmail())) {
			throw new DuplicateUserException(String.format("Email %s already been used", registerRequest.getEmail()));
		}

		userService.saveUser(mapRegisterRequestToUser(registerRequest));

		String token = authenticateAndGetToken(registerRequest.getUsername(), new String(Base64.getDecoder().decode(registerRequest.getPassword())));
		return new AuthResponse(token);
	}

	private String authenticateAndGetToken(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		return tokenProvider.generate(authentication);
	}

	private User mapRegisterRequestToUser(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(new String(Base64.getDecoder().decode(registerRequest.getPassword()))));
		user.setEmail(registerRequest.getEmail());
		user.setRole(SecurityConfiguration.USER);
		return user;
	}

}
