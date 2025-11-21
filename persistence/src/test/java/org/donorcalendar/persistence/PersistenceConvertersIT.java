package org.donorcalendar.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Transactional
public class PersistenceConvertersIT extends AbstractPersistenceIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testPersistenceConverters() {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUserId(IdGenerator.generateNewId());
        userProfileEntity.setName("John Doe " + userProfileEntity.getUserId());
        userProfileEntity.setEmail(userProfileEntity.getUserId() + "johntest@test.com");
        userProfileEntity.setLastDonation(LocalDate.now());
        userProfileEntity.setBloodType(BloodType.AB_POSITIVE);
        userProfileEntity.setUserStatus(UserStatus.DONOR);

        userProfileRepository.save(userProfileEntity);
        // This forces the method convertToDatabaseColumn of converters to be called
        em.flush();
        em.clear();
        // At this point method convertToEntityAttribute of converters will be called
        UserProfileEntity savedUserProfile = userProfileRepository.findById(userProfileEntity.getUserId()).orElse(new UserProfileEntity());

        assertEquals(userProfileEntity.getBloodType(), savedUserProfile.getBloodType());
        assertEquals(userProfileEntity.getLastDonation(), savedUserProfile.getLastDonation());
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT blood_type, last_donation FROM user_profile WHERE user_id = ?", userProfileEntity.getUserId());
        assertEquals(userProfileEntity.getBloodType().getValue(), result.get("blood_type"));
        assertEquals(java.sql.Date.valueOf(userProfileEntity.getLastDonation()), result.get("last_donation"));
    }


}
