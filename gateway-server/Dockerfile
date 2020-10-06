FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} gatewayservice.jar
ENTRYPOINT ["java","-jar","/gatewayservice.jar"]