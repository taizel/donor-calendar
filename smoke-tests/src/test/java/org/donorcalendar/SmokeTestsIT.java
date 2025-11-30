package org.donorcalendar;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.rest.dto.NewUserDto;
import org.donorcalendar.rest.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class SmokeTestsIT extends DatabaseContainerStarter {

    private static final GenericContainer<?> donorCalendarWebApp = new GenericContainer("donor-calendar:latest").
            withNetwork(databaseContainer.getNetwork()).
            withExposedPorts(8080).
            withEnv("DB_HOST", DB_HOST).
            withEnv("DB_PORT", "5432").
            withEnv("DB_NAME", databaseContainer.getDatabaseName()).
            withEnv("DB_USERNAME", databaseContainer.getUsername()).
            withEnv("DB_PASSWORD", databaseContainer.getPassword()).
            waitingFor(Wait.forLogMessage(".*" + Application.APPLICATION_READY + "\\n", 1)).
            withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw"));

    private static final String USER_PATH = "/user";
    // Not using WebTestClient as we are doing full blackbox test
    private static RestClient client;

    @BeforeAll
    static void setUp() {
        donorCalendarWebApp.start();

        String host = donorCalendarWebApp.getHost();
        Integer port = donorCalendarWebApp.getMappedPort(8080);

        client = RestClient.builder()
                .baseUrl("http://" + host + ":" + port)
                .build();
    }

    @Test
    void canCreateNewUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(2));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        // Create the new user
        var createResponse = client.post()
                .uri(USER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newUserDto)
                .retrieve()
                .toBodilessEntity();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Retrieve created user with basic auth
        var userResponse = client.get()
                .uri(USER_PATH)
                .headers(h -> h.setBasicAuth(newUserDto.getEmail(), newUserDto.getPassword()))
                .retrieve()
                .body(UserResponseDto.class);

        assertThat(userResponse)
                .extracting(UserResponseDto::getName, UserResponseDto::getEmail, UserResponseDto::getBloodType)
                .containsExactly(newUserDto.getName(), newUserDto.getEmail(), newUserDto.getBloodType());

    }

    @Test
    void forbiddenAccessErrorTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("forbidden@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(5));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        // Create user
        var createResponse = client.post()
                .uri(USER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newUserDto)
                .retrieve()
                .toBodilessEntity();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Now try forbidden access
        var forbiddenResponse = client.get()
                .uri("/donor-zone")
                .headers(h -> h.setBasicAuth(newUserDto.getEmail(), newUserDto.getPassword()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                }) // prevent throwing exception
                .toBodilessEntity();

        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}