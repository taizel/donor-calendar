package org.donorcalendar.rest;

import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.security.UserSecurityDetails;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/donor-zone", produces = MediaType.APPLICATION_JSON_VALUE)
public class DonorZoneController {

    @GetMapping
    public List<String> getShoppingFacilities(@AuthenticationPrincipal UserSecurityDetails userDetails) throws ForbiddenAccessException {
        if (!userDetails.getUserProfile().getUserStatus().equals(UserStatus.NEED_TO_DONATE)) {
            return Arrays.asList("Cool Restaurant", "Nice Burger Place", "Amazing Bakery");
        } else {
            throw new ForbiddenAccessException("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.");
        }
    }
}
