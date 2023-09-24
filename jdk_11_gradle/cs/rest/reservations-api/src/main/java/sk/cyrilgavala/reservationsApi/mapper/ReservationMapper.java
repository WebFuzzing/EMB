package sk.cyrilgavala.reservationsApi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sk.cyrilgavala.reservationsApi.model.Reservation;
import sk.cyrilgavala.reservationsApi.web.request.CreateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.request.UpdateReservationRequest;
import sk.cyrilgavala.reservationsApi.web.response.ReservationResponse;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

	@Mapping(target = "uuid", ignore = true)
	Reservation createRequestToModel(CreateReservationRequest request);

	@Mapping(target = "createdAt", ignore = true)
	Reservation updateRequestToModel(@MappingTarget Reservation model, UpdateReservationRequest request);

	ReservationResponse modelToResponse(Reservation model);

}
