package sk.cyrilgavala.reservationsApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Document(collection = "reservations")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reservation {

	@Id
	private String uuid;
	@Field(targetType = FieldType.STRING)
	private String reservationFor;
	@Field(targetType = FieldType.DATE_TIME)
	private LocalDateTime reservationFrom;
	@Field(targetType = FieldType.DATE_TIME)
	private LocalDateTime reservationTo;
	@Field(targetType = FieldType.DATE_TIME)
	private LocalDateTime createdAt;

}
