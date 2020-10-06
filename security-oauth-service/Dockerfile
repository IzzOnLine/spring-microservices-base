FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} oauthservice.jar
ENTRYPOINT ["java","-jar","/oauthservice.jar"]