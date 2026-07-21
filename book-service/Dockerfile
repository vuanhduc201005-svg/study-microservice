FROM amazoncorretto:17.0.17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} book-service.jar
ENTRYPOINT ["java", "-jar", "book-service.jar"]
EXPOSE 9001