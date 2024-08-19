FROM openjdk:17-jdk-alpine
COPY target/arnuv-0.0.1-SNAPSHOT.jar arnuv-service.jar
ENTRYPOINT ["java", "-jar", "arnuv-service.jar"]