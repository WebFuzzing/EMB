package sk.cyrilgavala.reservationsApi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import sk.cyrilgavala.reservationsApi.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {

	Reservation findByUuid(String reservationUuid);

	List<Reservation> findAllByReservationFor(String reservationFor, Sort sort);

	@Query("{ $or: [ {$and: [{'reservationFrom': {$lt: ?0}}, {'reservationTo': {$gt:  ?0}}]}, {$and: [{'reservationFrom': {$lt: ?1}}, {'reservationTo': {$gt:  ?1}}]}] }")
	List<Reservation> findAllBetween(LocalDateTime from, LocalDateTime to);
}
