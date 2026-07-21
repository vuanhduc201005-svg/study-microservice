FROM amazoncorretto:17.0.17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
EXPOSE 9003