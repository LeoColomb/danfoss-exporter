FROM maven:3-eclipse-temurin-21-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package assembly:single

FROM eclipse-temurin:25-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY .env.example /opt/app/.env
COPY --from=build /home/app/target/danfoss-exporter-1.0.0-jar-with-dependencies.jar /opt/app/danfoss-exporter.jar
ENTRYPOINT ["java","--add-opens=java.base/java.nio=ALL-UNNAMED","-jar","/opt/app/danfoss-exporter.jar"]
