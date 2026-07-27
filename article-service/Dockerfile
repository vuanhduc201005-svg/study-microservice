FROM amazoncorretto:17.0.17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} article-service.jar
ENTRYPOINT ["java", "-jar", "article-service.jar"]
EXPOSE 9006