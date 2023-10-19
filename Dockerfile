FROM openjdk:17-alpine
EXPOSE 8080
ADD target/housemate.jar housemate.jar
ENTRYPOINT ["java", "-jar", "/housemate.jar"]