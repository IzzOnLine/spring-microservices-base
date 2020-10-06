FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} microservice.jar
ENTRYPOINT ["java","-jar","/microservice.jar"]