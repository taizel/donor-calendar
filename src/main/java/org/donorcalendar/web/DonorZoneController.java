package org.donorcalendar.web;

import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.security.UserAuthenticationDetails;
import org.donorcalendar.service.UserService;
import org.donorcalendar.web.dto.ShoppingFacilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/donor-zone")
public class DonorZoneController {

    @RequestMapping(method = RequestMethod.GET)
    public ShoppingFacilities getShoppingFacilities(@AuthenticationPrincipal UserAuthenticationDetails userDetails) throws ForbiddenAccessException {
        if (!userDetails.getUserProfile().getUserStatus().equals(UserStatus.NEED_TO_DONATE)) {
            ShoppingFacilities facilitiesList = new ShoppingFacilities();
            facilitiesList.setShoppingFacilities(Arrays.asList("Cool Restaurant", "Nice Burger Place", "Amazing Bakery"));
            return facilitiesList;
        } else {
            throw new ForbiddenAccessException("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.");
        }
    }
}
