package org.api.library.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(value= {DataNotFoundException.class})
	public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException e){
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		CustomExceptionModel  customExceptionModel = new CustomExceptionModel(
								e.getMessage(), 
								httpStatus,
								ZonedDateTime.now(ZoneId.of("Z")),
								HttpServletResponse.SC_NOT_FOUND
								);
		return new ResponseEntity<>(customExceptionModel,httpStatus);
	
	}
}
