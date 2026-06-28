# Fase de compilación
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew :server:build -x test --no-daemon

# Fase de ejecución
FROM openjdk:17-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/server/build/libs/server-1.0.0-all.jar /app/ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]