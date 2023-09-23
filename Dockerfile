FROM openjdk:17
EXPOSE 8080
ADD target/backend-0.0.1-SNAPSHOT.jar backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/backend-0.0.1-SNAPSHOT.jar"]