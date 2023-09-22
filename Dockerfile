FROM openjdk:17
EXPOSE 8080
ADD target/.jar .jar
ENTRYPOINT ["java", "-jar", "/.jar"]