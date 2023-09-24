package sk.cyrilgavala.reservationsApi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sk.cyrilgavala.reservationsApi.model.User;
import sk.cyrilgavala.reservationsApi.security.TokenProvider;
import sk.cyrilgavala.reservationsApi.service.UserService;
import sk.cyrilgavala.reservationsApi.web.request.LoginRequest;
import sk.cyrilgavala.reservationsApi.web.request.RegisterRequest;
import sk.cyrilgavala.reservationsApi.web.response.AuthResponse;
import sk.cyrilgavala.reservationsApi.web.response.UserResponse;

import java.util.Base64;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for {@link UserRestController}.
 */
@WebMvcTest
@ActiveProfiles("test")
public class UserRestControllerTests {

	private static final String USERNAME = "username";
	private static final String PASSWORD_ENCODED = Base64.getEncoder().encodeToString("password".getBytes());
	private static final String PASSWORD_DECODED = "password";
	private static final String PASSWORD_HASHED = "$2a$10$VmtzKs.GroqLKkd4nzhbZ.F2LS4j3qQsR5afWeiID52GpQTR/vv8S";
	private static final String EMAIL = "user@test.com";
	private static final String ACCESS_TOKEN = "thisIsGeneratedJwtAccessToken";
	private static final String ERROR_CONTENT_TYPE = "text/plain;charset=UTF-8";
	private static final String DUPLICATE_USERNAME_ERROR_MESSAGE = String.format("Username %s already been used", USERNAME);
	private static final String DUPLICATE_EMAIL_ERROR_MESSAGE = String.format("Email %s already been used", EMAIL);

	private final LoginRequest loginRequest = new LoginRequest(USERNAME, PASSWORD_ENCODED);
	private final RegisterRequest registerRequest = new RegisterRequest(USERNAME, EMAIL, PASSWORD_ENCODED);

	private final ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.registerModule(new Jdk8Module())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	@MockBean
	private UserService userService;
	@MockBean
	private AuthenticationManager authenticationManager;
	@MockBean
	private TokenProvider tokenProvider;
	@MockBean
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void login_validRequest_pass() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD_DECODED);
		AuthResponse response = new AuthResponse(ACCESS_TOKEN);
		when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
		when(tokenProvider.generate(authentication)).thenReturn(ACCESS_TOKEN);

		mockMvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	void register_usernameExists_409() throws Exception {
		when(userService.existsByUsername(USERNAME)).thenReturn(true);

		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(registerRequest)))
			.andExpect(status().isConflict())
			.andExpect(header().string("Content-Type", ERROR_CONTENT_TYPE))
			.andExpect(content().string(DUPLICATE_USERNAME_ERROR_MESSAGE));
	}

	@Test
	void register_emailExists_409() throws Exception {
		when(userService.existsByUsername(USERNAME)).thenReturn(false);
		when(userService.existsByEmail(EMAIL)).thenReturn(true);

		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(registerRequest)))
			.andExpect(status().isConflict())
			.andExpect(header().string("Content-Type", ERROR_CONTENT_TYPE))
			.andExpect(content().string(DUPLICATE_EMAIL_ERROR_MESSAGE));
	}

	@Test
	void register_validRequest_pass() throws Exception {
		User userToSave = new User(null, USERNAME, EMAIL, PASSWORD_HASHED, "USER");
		Authentication authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD_DECODED);
		AuthResponse response = new AuthResponse(ACCESS_TOKEN);

		when(userService.existsByUsername(USERNAME)).thenReturn(false);
		when(userService.existsByEmail(EMAIL)).thenReturn(false);
		when(passwordEncoder.encode(PASSWORD_ENCODED)).thenReturn(PASSWORD_HASHED);
		when(userService.saveUser(userToSave)).thenReturn(new UserResponse());
		when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
		when(tokenProvider.generate(authentication)).thenReturn(ACCESS_TOKEN);

		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(registerRequest)))
			.andExpect(status().isCreated())
			.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}
}
