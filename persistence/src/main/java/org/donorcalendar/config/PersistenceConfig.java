package org.donorcalendar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("/persistence.properties")
public class PersistenceConfig {
}
