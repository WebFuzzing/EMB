package br.com.codenation.hospital.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ObjectNotFoundException extends RuntimeException{
	//auxilar para tratar exceção
	private static final long serialVersionUID = -7915253746748523406L;
	
	public ObjectNotFoundException(String msg) {
		super(msg);
	}
}