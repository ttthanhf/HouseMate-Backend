FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080
COPY target/housemate.jar housemate.jar
ENTRYPOINT ["java", "-jar", "/housemate.jar"]