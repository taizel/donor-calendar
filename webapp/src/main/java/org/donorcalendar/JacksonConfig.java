package org.donorcalendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class JacksonConfig {

    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configOverride(LocalDate.class).
                setFormat(JsonFormat.Value.forPattern(LOCAL_DATE_FORMAT));
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static ObjectMapper getNewCustomObjectMapper() {
        return buildObjectMapper();
    }

    @Bean
    public ObjectMapper getCustomObjectMapper() {
        return buildObjectMapper();
    }
}