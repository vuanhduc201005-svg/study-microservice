FROM amazoncorretto:17.0.17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} borrowing-service.jar
ENTRYPOINT ["java", "-jar", "borrowing-service.jar"]
EXPOSE 9004