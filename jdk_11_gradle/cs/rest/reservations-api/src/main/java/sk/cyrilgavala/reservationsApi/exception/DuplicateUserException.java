package sk.cyrilgavala.reservationsApi.exception;

public class DuplicateUserException extends RuntimeException {

	private static final long serialVersionUID = 9197342664218222132L;

	public DuplicateUserException(String message) {
		super(message);
	}
}
