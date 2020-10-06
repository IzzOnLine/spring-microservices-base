FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} configservice.jar
ENTRYPOINT ["java","-jar","/configservice.jar"]