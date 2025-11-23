# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:21

# Instalamos docker-cli para poder ejecutar docker exec hacia mariadb
RUN apt-get update && apt-get install -y docker.io && apt-get clean

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
