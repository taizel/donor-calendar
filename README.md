# Donor Calendar

[![Build Status](https://travis-ci.org/taizel/donor-calendar.svg?branch=master)](https://travis-ci.org/taizel/donor-calendar)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=donor-calendar&metric=alert_status)](https://sonarcloud.io/dashboard?id=donor-calendar)

Spring based project of a web application to remind users to donate blood.

This project is mainly used to experiment with Spring while also serving as a personal reference implementation.

The database containers started for integrations tests (persistence only) and smoke tests are configured to use in-memory file system (TmpFs) for performance reasons.

Two pages to create new donors are available as static resources under /js (http://localhost:8080/js when using the provided docker-compose.yml file).
- The mithril-index.html which uses [Mithril](https://mithril.js.org/) 
- The react-index.html which uses [React](https://reactjs.org/)

Those are simple pages added for basic experiments and tests with Mithril and React.

## Dependencies extra project
- [Java Development Kit 8](https://openjdk.java.net/projects/jdk8) 
- [Maven](https://maven.apache.org)
- [Docker](https://www.docker.com)
- [Docker Compose](https://github.com/docker/compose) - Optional, but recommended for simple execution

## Main project dependencies
- [Spring Boot](https://spring.io/projects/spring-boot) - Main project framework
- [Testcontainers](https://www.testcontainers.org) - To quickly start, configure and remove database Docker containers for testing
- [Flyway](https://flywaydb.org/documentation/plugins/springboot) - For database versioning (relying on Spring Boot integration)
- [REST Assured](http://rest-assured.io) - For end to end tests
- [PostgreSQL JDBC](https://jdbc.postgresql.org/) - For connection with PostgreSQL database

## Running the application
On the root folder, one simple way to start the project is:
- Build it with Maven (`mvn clean package` at least)
- After you can start it with `docker-compose`
