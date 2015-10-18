package org.donorcalendar.web;

import org.donorcalendar.service.UserService;
import org.donorcalendar.validation.ClientErrorInformation;
import org.donorcalendar.validation.ValidationException;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserDto saveUser(@RequestBody UserDto userDto) throws ValidationException {
        userService.saveUser(userDto);

        System.out.println("saved: " + userDto);
        return userDto;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformation> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformation error = new ClientErrorInformation(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
