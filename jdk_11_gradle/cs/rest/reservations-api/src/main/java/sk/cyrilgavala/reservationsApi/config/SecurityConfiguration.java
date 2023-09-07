package sk.cyrilgavala.reservationsApi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sk.cyrilgavala.reservationsApi.security.TokenAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	private final TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/api/reservation/**").hasAnyAuthority(ADMIN, USER)
			.antMatchers(HttpMethod.POST, "/api/reservation").hasAnyAuthority(ADMIN, USER)
			.antMatchers(HttpMethod.PUT, "/api/reservation").hasAnyAuthority(ADMIN, USER)
			.antMatchers(HttpMethod.DELETE, "/api/reservation/**").hasAnyAuthority(ADMIN, USER)
			.antMatchers(HttpMethod.GET, "/api/reservation").hasAuthority(ADMIN)
			.antMatchers("/api/user/**").permitAll()
			.antMatchers("/", "/error", "/documentation", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
			.anyRequest().authenticated();
		http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.cors().and().csrf().disable();
		return http.build();
	}

}
