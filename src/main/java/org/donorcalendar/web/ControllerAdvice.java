package org.donorcalendar.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.donorcalendar.web.dto.ClientErrorInformationDto;
import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ControllerAdvice {
    private final Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformationDto> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformationDto error = new ClientErrorInformationDto(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ClientErrorInformationDto> handleForbiddenAccessError(HttpServletRequest req, ForbiddenAccessException e) {
        ClientErrorInformationDto error = new ClientErrorInformationDto(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ClientErrorInformationDto> handleNotFoundError(HttpServletRequest req, NotFoundException e) {
        ClientErrorInformationDto error = new ClientErrorInformationDto(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ClientErrorInformationDto> handleInternalServerError(HttpServletRequest req, Throwable t) {
        ClientErrorInformationDto error = new ClientErrorInformationDto("Unexpected internal error.", req.getRequestURI(), req.getMethod());
        logger.error("Unexpected internal error, Throwable message is: " + t.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
