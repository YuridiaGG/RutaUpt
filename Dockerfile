# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
# Construimos solo el módulo del servidor
RUN ./gradlew :server:installDist --no-daemon

# ---------- Run Stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Exponemos el puerto que Railway inyectará
EXPOSE 8080

# Copiamos la distribución instalada del servidor
COPY --from=build /app/server/build/install/server /app/server

# Ejecutamos el servidor usando el script generado por Gradle
WORKDIR /app/server/bin
CMD ["./server"]
