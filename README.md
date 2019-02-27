# Donor Calendar

[![Build Status](https://travis-ci.org/taizel/donor-calendar.svg?branch=master)](https://travis-ci.org/taizel/donor-calendar)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=donor-calendar&metric=alert_status)](https://sonarcloud.io/dashboard?id=donor-calendar)

Spring based project of a web application to remind users to donate blood.

This project is mainly used to experiment with Spring while also serving as a personal reference implementation.

It has the same set of unit tests using mocks and stubbing, solely to compare both approaches. The database containers started for integrations tests are configured to use in-memory file system (TmpFs) for performance reasons.

A page to save new donors using [Mithril](https://mithril.js.org/) is available as a static resource at js/mithril-index.html (http://localhost:8080/js/mithril-index.html if you are using the provided docker-compose.yml file). It is a very simple page added for basic experimentation with Mithril.  

## Dependencies extra project
- [Java Development Kit 8](https://openjdk.java.net/projects/jdk8) 
- [Maven](https://maven.apache.org)
- [Docker](https://www.docker.com)
- [Docker Compose](https://github.com/docker/compose) - Optional, but recommended for simplicity on execution.

## Main project dependencies
- [Spring Boot](https://spring.io/projects/spring-boot) - Main project framework.
- [Testcontainers](https://www.testcontainers.org) - To quickly start, configure and remove database Docker containers for testing.
- [Flyway](https://flywaydb.org/documentation/plugins/springboot) - For database versioning (relying on Spring Boot integration).
- [REST Assured](http://rest-assured.io) - For end to end tests.
- [PostgreSQL JDBC](https://jdbc.postgresql.org/) - For connection with PostgreSQL database.

## Running the application
On the root folder, simplest way to start the project:
- Build it with Maven (`mvn clean package` at least)
- After you can start it with `docker-compose`
