package org.donorcalendar.rest;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.rest.dto.NewUserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

class ControllerAdviceIT extends AbstractRestIntegrationTest {

    @Override
    protected void setUp() {
        // No setup required
    }

    @AfterEach
    @Override
    public void tearDown() {
        // No teardown required
    }

    @Test
    void validationErrorTest() {
        NewUserDto newUserDto = new NewUserDto();

        client.post()
                .uri("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorMessage")
                .isEqualTo("Password cannot be empty.");
    }

    @Test
    void forbiddenAccessErrorTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(5));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        // --- Create user ---
        client.post()
                .uri("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUserDto)
                .exchange()
                .expectStatus().isOk();

        // --- Access donor-zone with basic auth (should be forbidden) ---
        client.get()
                .uri("/donor-zone")
                .headers(h -> h.setBasicAuth(newUserDto.getEmail(), newUserDto.getPassword()))
                .exchange()
                .expectStatus().isForbidden(); // 403 FORBIDDEN
    }
}
