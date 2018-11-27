# Donor Calendar
Spring based project of a web application to remind users to donate blood.

This project is mainly used to experiment with Spring while also serving as a personal reference implementation.

It has the same set of unit tests using mocks and stubbing solely to compare both approaches, keeping both ways helps to evaluate how the application is designed and how it is evolving. The database containers started for integrations tests are configured to use in-memory file system (TmpFs) for performance reasons.

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
