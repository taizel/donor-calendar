package org.donorcalendar.rest;

import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.security.UserSecurityDetails;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DonorZoneControllerTest {

    private final DonorZoneController target = new DonorZoneController();

    @Test
    public void testGetShoppingFacilitiesAccessAllowed() {
        UserProfile userProfile = new UserProfile.UserProfileBuilder(1, null, null, null, UserStatus.DONOR).build();
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

        try {
            target.getShoppingFacilities(userSecurityDetails);
        } catch (ForbiddenAccessException e) {
            fail();
        }
    }

    @Test
    public void testGetShoppingFacilitiesForbiddenAccess() {
        UserProfile userProfile = new UserProfile.UserProfileBuilder(1, null, null, null, UserStatus.NEED_TO_DONATE).build();
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

        try {
            target.getShoppingFacilities(userSecurityDetails);
            fail();
        } catch (ForbiddenAccessException e) {
            assertEquals("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.", e.getMessage());
        }
    }

}
