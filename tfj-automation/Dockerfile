# Build stage
FROM maven:3.9-openjdk-17 AS build
WORKDIR /app/backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/backend/target/tfj-automation-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
