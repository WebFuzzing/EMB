package sk.cyrilgavala.reservationsApi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank
	private String username;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
}
