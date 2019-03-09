package org.donorcalendar.web;

import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.security.UserAuthenticationDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/donor-zone")
public class DonorZoneController {

    @GetMapping
    public List<String> getShoppingFacilities(@AuthenticationPrincipal UserAuthenticationDetails userDetails) throws ForbiddenAccessException {
        if (!userDetails.getUserProfile().getUserStatus().equals(UserStatus.NEED_TO_DONATE)) {
            return Arrays.asList("Cool Restaurant", "Nice Burger Place", "Amazing Bakery");
        } else {
            throw new ForbiddenAccessException("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.");
        }
    }
}
