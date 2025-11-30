package org.donorcalendar.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@TestPropertySource(properties = {"org.donorcalendar.scheduling.enable = false"}) // disable scheduled jobs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractRestIntegrationTest {

    @LocalServerPort
    protected int port;

    protected WebTestClient client;

    @BeforeEach
    void completeSetUp() {
        this.client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
        setUp();
    }

    protected abstract void setUp();

    @AfterEach
    public abstract void tearDown();
}
