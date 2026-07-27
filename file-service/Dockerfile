FROM amazoncorretto:17.0.17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} file-service.jar
ENTRYPOINT ["java", "-jar", "file-service.jar"]
EXPOSE 9007