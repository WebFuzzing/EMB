package sk.cyrilgavala.reservationsApi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import sk.cyrilgavala.reservationsApi.exception.ReservationException;
import sk.cyrilgavala.reservationsApi.mapper.ReservationMapper;
import sk.cyrilgavala.reservationsApi.model.Reservation;
import sk.cyrilgavala.reservationsApi.repository.ReservationRepository;
import sk.cyrilgavala.reservationsApi.web.request.CreateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.request.UpdateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.response.ReservationResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link ReservationService}.
 */
@ExtendWith(value = MockitoExtension.class)
public class ReservationServiceImplTests {

	private static final LocalDateTime DATE_15_00 = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1).withHour(15);
	private static final LocalDateTime DATE_16_00 = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1).withHour(16);
	private static final LocalDateTime DATE_17_00 = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1).withHour(17);
	private static final String RESERVATION_UUID = "reservationUuid";
	private static final String USERNAME = "username";
	private static final String REVERTED_DATES_ERROR_MESSAGE = "Reservation unprocessable: start date is after end date";
	private static final String RESERVATION_EXISTS_ERROR_MESSAGE = "Reservation unprocessable: covers another reservation";
	private static final String START_IN_PAST_ERROR_MESSAGE = "Reservation unprocessable: start date is in past";
	private final CreateReservationRequest createRequest = new CreateReservationRequest();
	private final UpdateReservationRequest updateRequest = new UpdateReservationRequest();

	@Mock
	private transient ReservationRepository repository;
	@Mock
	private ReservationMapper mapper;
	@InjectMocks
	private ReservationServiceImpl service;

	@Test
	void create_invalidDatesFromAfterTo_errorThrown() {
		createRequest.setReservationFrom(DATE_17_00);
		createRequest.setReservationTo(DATE_15_00);

		try {
			service.createReservation(createRequest);
			fail("Reservation exception should be thrown");
		} catch (ReservationException exception) {
			assertEquals(REVERTED_DATES_ERROR_MESSAGE, exception.getMessage(), "Different message");

			verifyNoMoreInteractions(repository);
		}
	}

	@Test
	void create_invalidDatesStartInPast_errorThrown() {
		createRequest.setReservationFrom(DATE_17_00.minusDays(2));
		createRequest.setReservationTo(DATE_15_00);

		try {
			service.createReservation(createRequest);
			fail("Reservation exception should be thrown");
		} catch (ReservationException exception) {
			assertEquals(START_IN_PAST_ERROR_MESSAGE, exception.getMessage(), "Different message");

			verifyNoMoreInteractions(repository);
		}
	}

	@Test
	void create_invalidDatesReservationExists_errorThrown() {
		createRequest.setReservationFrom(DATE_15_00);
		createRequest.setReservationTo(DATE_17_00);

		when(repository.findAllBetween(DATE_15_00, DATE_17_00)).thenReturn(Collections.singletonList(new Reservation()));

		try {
			service.createReservation(createRequest);
			fail("Reservation exception should be thrown");
		} catch (ReservationException exception) {
			assertEquals(RESERVATION_EXISTS_ERROR_MESSAGE, exception.getMessage(), "Different message");

			verifyNoMoreInteractions(repository);
		}
	}

	@Test
	void create_validRequest_pass() {
		Reservation reservationModel = new Reservation("uuid", "username", DATE_15_00, DATE_17_00, DATE_15_00);
		createRequest.setReservationFrom(DATE_15_00);
		createRequest.setReservationTo(DATE_17_00);

		when(repository.findAllBetween(DATE_15_00, DATE_17_00)).thenReturn(Collections.emptyList());
		when(mapper.createRequestToModel(createRequest)).thenReturn(reservationModel);
		when(repository.insert(reservationModel)).thenReturn(reservationModel);
		when(mapper.modelToResponse(reservationModel))
			.thenReturn(new ReservationResponse(null, null, DATE_15_00, DATE_17_00));

		try {
			ReservationResponse result = service.createReservation(createRequest);

			assertEquals(DATE_15_00, result.getReservationFrom());
			assertEquals(DATE_17_00, result.getReservationTo());

			verifyNoMoreInteractions(repository);
		} catch (ReservationException exception) {
			fail("Reservation exception should not be thrown");
		}
	}

	@Test
	void update_validRequest_pass() {
		updateRequest.setReservationFrom(DATE_16_00);
		updateRequest.setReservationTo(DATE_17_00);
		updateRequest.setUuid(RESERVATION_UUID);
		Reservation reservationToReturn = new Reservation(RESERVATION_UUID, null, DATE_16_00, DATE_17_00, DATE_15_00);
		Reservation reservationFromDb = new Reservation(RESERVATION_UUID, null, DATE_15_00, DATE_17_00, DATE_15_00);

		when(repository.findAllBetween(DATE_16_00, DATE_17_00)).thenReturn(Collections.emptyList());
		when(repository.findByUuid(RESERVATION_UUID)).thenReturn(reservationFromDb);
		when(repository.save(reservationToReturn)).thenReturn(reservationToReturn);
		when(mapper.updateRequestToModel(reservationFromDb, updateRequest)).thenReturn(reservationToReturn);
		when(mapper.modelToResponse(reservationToReturn))
			.thenReturn(new ReservationResponse(RESERVATION_UUID, null, DATE_16_00, DATE_17_00));

		try {
			ReservationResponse result = service.updateReservation(updateRequest);

			assertEquals(DATE_16_00, result.getReservationFrom());
			assertEquals(DATE_17_00, result.getReservationTo());

			verifyNoMoreInteractions(repository);
		} catch (ReservationException exception) {
			fail("Reservation exception should not be thrown");
		}
	}

	@Test
	void delete_validUuid_pass() {
		service.deleteReservation(RESERVATION_UUID);
		verify(repository).deleteById(RESERVATION_UUID);
	}

	@Test
	void getAllReservations_pass() {
		when(repository.findAll(Sort.by("reservationFrom").descending()))
			.thenReturn(Arrays.asList(
				new Reservation(RESERVATION_UUID + "1", USERNAME, DATE_15_00, DATE_16_00, DATE_15_00),
				new Reservation(RESERVATION_UUID + "2", USERNAME, DATE_16_00, DATE_17_00, DATE_16_00)));
		when(mapper.modelToResponse(any(Reservation.class)))
			.thenReturn(
				new ReservationResponse(RESERVATION_UUID + "1", USERNAME, DATE_15_00, DATE_16_00),
				new ReservationResponse(RESERVATION_UUID + "2", USERNAME, DATE_16_00, DATE_17_00));

		List<ReservationResponse> result = service.getAllReservations();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getUuid()).isEqualTo(RESERVATION_UUID + "1");
		assertThat(result.get(0).getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.get(0).getReservationFrom()).isEqualTo(DATE_15_00);
		assertThat(result.get(0).getReservationTo()).isEqualTo(DATE_16_00);
		assertThat(result.get(1).getUuid()).isEqualTo(RESERVATION_UUID + "2");
		assertThat(result.get(1).getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.get(1).getReservationFrom()).isEqualTo(DATE_16_00);
		assertThat(result.get(1).getReservationTo()).isEqualTo(DATE_17_00);
	}

	@Test
	void getReservationsForUsername_pass() {
		when(repository.findAllByReservationFor(USERNAME, Sort.by("reservationFrom").descending()))
			.thenReturn(Arrays.asList(
				new Reservation(RESERVATION_UUID + "1", USERNAME, DATE_15_00, DATE_16_00, DATE_15_00),
				new Reservation(RESERVATION_UUID + "2", USERNAME, DATE_16_00, DATE_17_00, DATE_16_00)));
		when(mapper.modelToResponse(any(Reservation.class)))
			.thenReturn(
				new ReservationResponse(RESERVATION_UUID + "1", USERNAME, DATE_15_00, DATE_16_00),
				new ReservationResponse(RESERVATION_UUID + "2", USERNAME, DATE_16_00, DATE_17_00));

		List<ReservationResponse> result = service.getAllReservationsForUsername(USERNAME);
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getUuid()).isEqualTo(RESERVATION_UUID + "1");
		assertThat(result.get(0).getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.get(0).getReservationFrom()).isEqualTo(DATE_15_00);
		assertThat(result.get(0).getReservationTo()).isEqualTo(DATE_16_00);
		assertThat(result.get(1).getUuid()).isEqualTo(RESERVATION_UUID + "2");
		assertThat(result.get(1).getReservationFor()).isEqualTo(USERNAME);
		assertThat(result.get(1).getReservationFrom()).isEqualTo(DATE_16_00);
		assertThat(result.get(1).getReservationTo()).isEqualTo(DATE_17_00);
	}
}
