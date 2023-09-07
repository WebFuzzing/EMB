package sk.cyrilgavala.reservationsApi.web.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

	private static final long serialVersionUID = -3333137047634067175L;

	private String id;
	private String username;
	private String email;
	@JsonIgnore
	private String password;
	private String role;
}
