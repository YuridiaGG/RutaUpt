# Fase de compilación
FROM gradle:8.7-jdk21 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Dar permisos al wrapper
RUN chmod +x gradlew

RUN ./gradlew :server:build -x test --no-daemon

# Fase ejecución
FROM eclipse-temurin:21-jre

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]