FROM maven:3.9.16-eclipse-temurin-26-noble AS build

WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:26-jre-noble

RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/target/*.jar app.jar
COPY --chown=app:app docker/healthcheck.sh /app/healthcheck.sh
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
