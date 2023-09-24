package sk.cyrilgavala.reservationsApi.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.cyrilgavala.reservationsApi.web.DateTimeAware;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReservationResponse implements DateTimeAware, Serializable {

	private static final long serialVersionUID = 5714157215793550376L;

	private String uuid;
	private String reservationFor;
	@JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
	private LocalDateTime reservationFrom;
	@JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
	private LocalDateTime reservationTo;

}
