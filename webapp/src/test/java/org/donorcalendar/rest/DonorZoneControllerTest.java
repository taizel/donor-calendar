package org.donorcalendar.rest;

import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.security.UserSecurityDetails;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DonorZoneControllerTest {

    private final DonorZoneController target = new DonorZoneController();

    @Test
    void getShoppingFacilitiesAccessAllowed() {
        UserProfile userProfile = new UserProfile.UserProfileBuilder(1, null, null, null, UserStatus.DONOR).build();
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

        assertThatCode(() -> target.getShoppingFacilities(userSecurityDetails)).doesNotThrowAnyException();
    }

    @Test
    void getShoppingFacilitiesForbiddenAccess() {
        UserProfile userProfile = new UserProfile.UserProfileBuilder(1, null, null, null, UserStatus.NEED_TO_DONATE).build();
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

        assertThatThrownBy(() -> target.getShoppingFacilities(userSecurityDetails))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.");
    }

}
