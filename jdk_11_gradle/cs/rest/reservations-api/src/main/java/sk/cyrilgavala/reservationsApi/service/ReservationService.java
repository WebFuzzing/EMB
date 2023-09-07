package sk.cyrilgavala.reservationsApi.service;

import sk.cyrilgavala.reservationsApi.web.request.CreateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.request.UpdateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.response.ReservationResponse;

import java.util.List;

/**
 * Service for basic operations for reservations.
 */
public interface ReservationService {

	ReservationResponse createReservation(CreateReservationRequest request);

	ReservationResponse updateReservation(UpdateReservationRequest request);

	List<ReservationResponse> getAllReservations();

	List<ReservationResponse> getAllReservationsForUsername(String username);

	void deleteReservation(String reservationUuid);
}
