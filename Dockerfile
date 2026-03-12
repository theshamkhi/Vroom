# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY shared/pom.xml shared/pom.xml
COPY security/pom.xml security/pom.xml
COPY content/pom.xml content/pom.xml
COPY media/pom.xml media/pom.xml
COPY notification/pom.xml notification/pom.xml
COPY learning/pom.xml learning/pom.xml
COPY application/pom.xml application/pom.xml

RUN mvn -B -q -DskipTests=true dependency:go-offline

COPY shared shared
COPY security security
COPY content content
COPY media media
COPY notification notification
COPY learning learning
COPY application application

RUN mvn -B -DskipTests=true -pl application -am package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/application/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
