# Etapa de build
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test -x validateStructure --no-daemon

# Etapa de runtime
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
