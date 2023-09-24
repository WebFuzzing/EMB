package sk.cyrilgavala.reservationsApi.exception;

public class ReservationException extends RuntimeException {

	private static final long serialVersionUID = 4033799885256608552L;

	public ReservationException(String message) {
		super(message);
	}
}
