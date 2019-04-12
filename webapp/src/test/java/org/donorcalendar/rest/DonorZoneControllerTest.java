package org.donorcalendar.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.donorcalendar.model.ForbiddenAccessException;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.security.UserSecurityDetails;
import org.junit.Test;

public class DonorZoneControllerTest {

	private DonorZoneController target = new DonorZoneController();

	@Test
	public void testGetShoppingFacilitiesAccessAllowed() throws ForbiddenAccessException {
		UserProfile userProfile = new UserProfile();
		userProfile.setUserStatus(UserStatus.DONOR);
		UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

		target.getShoppingFacilities(userSecurityDetails);
	}

	@Test
	public void testGetShoppingFacilitiesForbiddenAccess() {
		UserProfile userProfile = new UserProfile();
		userProfile.setUserStatus(UserStatus.NEED_TO_DONATE);
		UserSecurityDetails userSecurityDetails = new UserSecurityDetails(userProfile, null);

		try {
			target.getShoppingFacilities(userSecurityDetails);
			fail();
		} catch (ForbiddenAccessException e) {
			assertEquals("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource.", e.getMessage());
		}
	}

}
