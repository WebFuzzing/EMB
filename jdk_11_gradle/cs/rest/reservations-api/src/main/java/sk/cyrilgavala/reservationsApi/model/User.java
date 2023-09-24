package sk.cyrilgavala.reservationsApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	private String id;
	@Field(targetType = FieldType.STRING)
	private String username;
	@Field(targetType = FieldType.STRING)
	private String email;
	@Field(targetType = FieldType.STRING)
	private String password;
	@Field(targetType = FieldType.STRING)
	private String role;
}
