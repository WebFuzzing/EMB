package sk.cyrilgavala.reservationsApi.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.cyrilgavala.reservationsApi.web.DateTimeAware;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateReservationRequest implements DateTimeAware, Serializable {

	private static final long serialVersionUID = 555607903324853344L;

	@NotBlank
	private String uuid;

	@NotBlank
	private String reservationFor;

	@NotNull
	@JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
	private LocalDateTime reservationFrom;

	@NotNull
	@JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
	private LocalDateTime reservationTo;

}
