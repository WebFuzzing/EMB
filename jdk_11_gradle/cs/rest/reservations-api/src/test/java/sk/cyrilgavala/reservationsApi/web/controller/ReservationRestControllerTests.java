package sk.cyrilgavala.reservationsApi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sk.cyrilgavala.reservationsApi.exception.ReservationException;
import sk.cyrilgavala.reservationsApi.service.ReservationService;
import sk.cyrilgavala.reservationsApi.web.request.CreateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.request.UpdateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.response.ReservationResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for {@link ReservationRestController}.
 */
@WebMvcTest
@ActiveProfiles("test")
public class ReservationRestControllerTests {

	private static final String UUID = "uuid";
	private static final String USERNAME = "username";
	private static final String BLANK_USERNAME = "   ";
	private static final LocalDateTime DATE_15_00 = LocalDateTime.of(2022, 5, 20, 15, 0, 0);
	private static final LocalDateTime DATE_16_00 = LocalDateTime.of(2022, 5, 20, 16, 0, 0);
	private static final String BLANK_USERNAME_ERROR_MESSAGE_GET = "getAllReservationsForUser.username: must not be blank";
	private static final String BLANK_UUID_ERROR_MESSAGE = "deleteReservation.reservationUuid: must not be blank";
	private static final String BLANK_USERNAME_ERROR_MESSAGE_POST = "Validation failed for argument";
	private static final String RESERVATION_UNPROCESSABLE_START_DATE_IS_AFTER_END_DATE = "Reservation unprocessable: start date is after end date";
	private static final String RESERVATION_UNPROCESSABLE_COVERS_ANOTHER_RESERVATION = "Reservation unprocessable: covers another reservation";
	private final ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.registerModule(new Jdk8Module())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	@MockBean
	private ReservationService service;
	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void get_reservationsForUser_pass() throws Exception {
		List<ReservationResponse> response = Collections.singletonList(
			new ReservationResponse(UUID, USERNAME, DATE_15_00, DATE_16_00));
		when(service.getAllReservationsForUsername(USERNAME)).thenReturn(response);

		mockMvc.perform(get("/api/reservation/" + USERNAME))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void get_reservationsForUser_passEmptyResponse() throws Exception {
		List<ReservationResponse> response = Collections.emptyList();
		when(service.getAllReservationsForUsername(USERNAME)).thenReturn(response);

		mockMvc.perform(get("/api/reservation/" + USERNAME))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void get_reservationsForUser_error_400() throws Exception {
		List<ReservationResponse> response = Collections.emptyList();
		when(service.getAllReservationsForUsername(USERNAME)).thenReturn(response);

		mockMvc.perform(get("/api/reservation/" + BLANK_USERNAME))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(BLANK_USERNAME_ERROR_MESSAGE_GET));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"ADMIN"})
	void get_reservations_pass() throws Exception {
		List<ReservationResponse> response = Collections.singletonList(new ReservationResponse(UUID, USERNAME, DATE_15_00, DATE_16_00));
		when(service.getAllReservations()).thenReturn(response);

		mockMvc.perform(get("/api/reservation"))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void delete_validUuid_pass() throws Exception {
		doNothing().when(service).deleteReservation(UUID);

		mockMvc.perform(delete("/api/reservation/" + UUID))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void delete_blankUuid_error_400() throws Exception {
		mockMvc.perform(delete("/api/reservation/" + BLANK_USERNAME))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(BLANK_UUID_ERROR_MESSAGE));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void create_validRequest_pass() throws Exception {
		CreateReservationRequest request = new CreateReservationRequest(USERNAME, DATE_15_00, DATE_16_00, DATE_15_00);
		ReservationResponse response = new ReservationResponse(UUID, USERNAME, DATE_15_00, DATE_16_00);
		when(service.createReservation(request)).thenReturn(response);

		mockMvc.perform(post("/api/reservation")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void create_invalidRequest_error_400() throws Exception {
		CreateReservationRequest request = new CreateReservationRequest(BLANK_USERNAME, DATE_15_00, DATE_16_00, DATE_15_00);

		boolean validResponseMessage = mockMvc.perform(post("/api/reservation")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString().startsWith(BLANK_USERNAME_ERROR_MESSAGE_POST);
		Assertions.assertTrue(validResponseMessage);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void create_invalidRequest_coveringReservationPresent_422() throws Exception {
		CreateReservationRequest request = new CreateReservationRequest(USERNAME, DATE_15_00, DATE_16_00, DATE_15_00);
		when(service.createReservation(request)).thenThrow(new ReservationException(RESERVATION_UNPROCESSABLE_COVERS_ANOTHER_RESERVATION));

		mockMvc.perform(post("/api/reservation")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().string(RESERVATION_UNPROCESSABLE_COVERS_ANOTHER_RESERVATION));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void create_invalidRequest_startDateAfterEndDate_422() throws Exception {
		CreateReservationRequest request = new CreateReservationRequest(USERNAME, DATE_16_00, DATE_15_00, DATE_15_00);
		when(service.createReservation(request)).thenThrow(new ReservationException(RESERVATION_UNPROCESSABLE_START_DATE_IS_AFTER_END_DATE));

		mockMvc.perform(post("/api/reservation")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().string(RESERVATION_UNPROCESSABLE_START_DATE_IS_AFTER_END_DATE));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = {"USER"})
	void update_validRequest_pass() throws Exception {
		UpdateReservationRequest request = new UpdateReservationRequest(UUID, USERNAME, DATE_15_00, DATE_16_00);
		ReservationResponse response = new ReservationResponse(UUID, USERNAME, DATE_15_00, DATE_16_00);
		when(service.updateReservation(request)).thenReturn(response);

		mockMvc.perform(put("/api/reservation")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}
}
