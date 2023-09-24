package sk.cyrilgavala.reservationsApi.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.reservationsApi.model.Reservation;
import sk.cyrilgavala.reservationsApi.web.request.CreateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.request.UpdateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.response.ReservationResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for {@link ReservationMapper}.
 */
@ExtendWith(MockitoExtension.class)
public class ReservationMapperTests {

	private static final String UUID = "reservationUuid";
	private static final String USERNAME = "username";
	private static final LocalDateTime DATE_15_00 = LocalDateTime.of(2022, 5, 20, 15, 0, 0);
	private static final LocalDateTime DATE_16_00 = LocalDateTime.of(2022, 5, 20, 16, 0, 0);

	private final ReservationMapper mapper = new ReservationMapperImpl();

	@Test
	void creteRequestToModel_validRequest() {
		CreateReservationRequest request = new CreateReservationRequest(USERNAME, DATE_15_00, DATE_16_00, DATE_15_00);
		Reservation result = mapper.createRequestToModel(request);
		assertThat(result != null).isTrue();
		assertThat(result.getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.getReservationFrom()).isEqualTo(DATE_15_00);
		assertThat(result.getReservationTo()).isEqualTo(DATE_16_00);
		assertThat(result.getCreatedAt()).isEqualTo(DATE_15_00);
	}

	@Test
	void updateRequestToModel_null() {
		Reservation result = mapper.updateRequestToModel(new Reservation(), null);
		assertThat(result).isNull();
	}

	@Test
	void updateRequestToModel_validRequest() {
		UpdateReservationRequest request = new UpdateReservationRequest(UUID, USERNAME, DATE_15_00, DATE_16_00);
		Reservation result = mapper.updateRequestToModel(new Reservation(), request);
		assertThat(result != null).isTrue();
		assertThat(result.getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.getReservationFrom()).isEqualTo(DATE_15_00);
		assertThat(result.getReservationTo()).isEqualTo(DATE_16_00);
		assertThat(result.getUuid()).isEqualTo(UUID);
	}

	@Test
	void createRequestToModel_null() {
		Reservation result = mapper.createRequestToModel(null);
		assertThat(result).isNull();
	}

	@Test
	void modelToResponse_validModel() {
		Reservation model = new Reservation(UUID, USERNAME, DATE_15_00, DATE_16_00, DATE_15_00);
		ReservationResponse result = mapper.modelToResponse(model);
		assertThat(result != null).isTrue();
		assertThat(result.getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.getReservationFrom()).isEqualTo(DATE_15_00);
		assertThat(result.getReservationTo()).isEqualTo(DATE_16_00);
		assertThat(result.getUuid()).isEqualTo(UUID);
	}

	@Test
	void modelToResponse_null() {
		ReservationResponse result = mapper.modelToResponse(null);
		assertThat(result).isNull();
	}
}
