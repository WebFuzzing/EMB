package br.com.codenation.hospital.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HospitalCheioException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public HospitalCheioException() {
        super("Hospital sem vagas para check in!");
    }

    public HospitalCheioException(String message) {
        super(message);
    }
}
