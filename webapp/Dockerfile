FROM openjdk:8

ARG JAR_FILE
ADD target/${JAR_FILE} /donor-calendar.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "/donor-calendar.jar"]