FROM openjdk:17
EXPOSE 8080
ADD target/housemate.jar housemate.jar
ENTRYPOINT ["java", "-jar", "/housemate.jar"]