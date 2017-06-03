package org.donorcalendar.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.donorcalendar.exception.ClientErrorInformation;
import org.donorcalendar.exception.ForbiddenAccessException;
import org.donorcalendar.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ControllerAdvice {
    private Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformation> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformation error = new ClientErrorInformation(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ClientErrorInformation> handleForbiddenAccessError(HttpServletRequest req, ForbiddenAccessException e) {
        ClientErrorInformation error = new ClientErrorInformation(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ClientErrorInformation> handleInternalServerError(HttpServletRequest req, Throwable t) {
        ClientErrorInformation error = new ClientErrorInformation("Unexpected internal error.", req.getRequestURI(), req.getMethod());
        logger.error("Unexpected internal error, Throwable.getMessage is: " + t.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
