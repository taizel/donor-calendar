package org.donorcalendar.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.rest.dto.ClientErrorInformationDto;
import org.owasp.encoder.Encode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    private static final Log logger = LogFactory.getLog(ControllerAdvice.class);

    private ClientErrorInformationDto buildClientErrorInformationDto(HttpServletRequest req, String message) {
        return new ClientErrorInformationDto(message,
                Encode.forHtml(req.getRequestURI()), req.getMethod());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformationDto> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformationDto error = buildClientErrorInformationDto(req, e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ClientErrorInformationDto> handleForbiddenAccessError(HttpServletRequest req, ForbiddenAccessException e) {
        ClientErrorInformationDto error = buildClientErrorInformationDto(req, e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /*
     * This case should never happen as Spring unauthorized error should happen before, but as it's part of the signature in some UserController
     * methods I decided to add it here just in case something unexpected happens.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ClientErrorInformationDto> handleNotFoundError(HttpServletRequest req, NotFoundException e) {
        ClientErrorInformationDto error = buildClientErrorInformationDto(req, e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ClientErrorInformationDto> handleInternalServerError(HttpServletRequest req, Throwable t) {
        ClientErrorInformationDto error = buildClientErrorInformationDto(req, "Unexpected internal error.");
        logger.error("Unexpected internal error.", t);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
