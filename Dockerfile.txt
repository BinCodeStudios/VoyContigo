# Etapa 1: Construcción del proyecto con Maven
  FROM maven:3.9.6-eclipse-temurin-21 AS build
  WORKDIR /app
  COPY VoyContigo_terminalOnline /app
  RUN mvn clean package -DskipTests
  
  # Etapa 2: Ejecución del JAR con Java 21
  FROM eclipse-temurin:21-jdk
  WORKDIR /app
  COPY --from=build /app/target/*.jar app.jar
  EXPOSE 8080
  CMD ["java", "-jar", "app.jar"]
