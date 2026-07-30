# Fase de compilación
FROM gradle:8.7-jdk21 AS build

# Fase de compilación
FROM gradle:8.5-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN chmod +x gradlew
RUN ./gradlew :server:build -x test --no-daemon

# Fase ejecución
FROM eclipse-temurin:21-jre

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/ktor-server.jar

ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/app/ktor-server.jar"]