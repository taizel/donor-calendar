package org.donorcalendar;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan(basePackages = "org.donorcalendar.persistence")
public class PersistenceIntegrationTestConfig {
}
