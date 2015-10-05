package org.donorcalendar.web;

import org.donorcalendar.service.UserService;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/user",method=RequestMethod.POST)
    public UserDto saveUser(@RequestBody UserDto userDto) {
        userService.saveUser(userDto);
        System.out.println("saved");
        System.out.println(userDto);
        return userDto;
    }

}
