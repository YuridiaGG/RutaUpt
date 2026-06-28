# Fase de compilación
FROM gradle:8.5-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test --no-daemon

# Fase de ejecución
FROM openjdk:17-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]