# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
RUN mvn -f backend/pom.xml dependency:go-offline -B
COPY backend/src backend/src
RUN mvn -f backend/pom.xml package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/backend/target/*.jar /app/app.jar
COPY bootstrap /app/bootstrap
RUN mkdir -p /app/chronicles

ENV SPRING_PROFILES_ACTIVE=postgres-dev
ENV CHUGALKHOR_BOOTSTRAP_FOLDER=/app/bootstrap
ENV CHUGALKHOR_CHRONICLE_FOLDER=/app/chronicles

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
