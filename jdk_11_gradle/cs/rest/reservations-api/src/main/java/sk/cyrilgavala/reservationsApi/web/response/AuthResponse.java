package sk.cyrilgavala.reservationsApi.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

	private String accessToken;
}
