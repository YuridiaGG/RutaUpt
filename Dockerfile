# ---------- Build ----------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :server:build -x test --no-daemon


# ---------- Run ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/server/build/libs/*.jar app.jar

CMD ["java","-jar","app.jar"]