# renovate: datasource=java-version depName=java-jdk packageName=java-jdk extractVersion=^(?<version>\d+)
ARG JAVA_VERSION=21
FROM maven:3-eclipse-temurin-${JAVA_VERSION}-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package assembly:single

FROM eclipse-temurin:${JAVA_VERSION}-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY .env.example /opt/app/.env
COPY --from=build /home/app/target/danfoss-exporter-1.0.0-jar-with-dependencies.jar /opt/app/danfoss-exporter.jar
ENTRYPOINT ["java","--add-opens=java.base/java.nio=ALL-UNNAMED","-jar","/opt/app/danfoss-exporter.jar"]
