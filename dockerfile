# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:21
WORKDIR /app
ENV TZ=America/Argentina/Buenos_Aires
RUN mkdir -p /app/backups
RUN apt-get update && apt-get install -y docker.io
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]