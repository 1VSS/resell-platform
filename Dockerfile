FROM maven:3.9-eclipse-temurin-23 AS build

WORKDIR /app

# Copy the POM file first and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Only package without running tests or compilation (to test Docker setup)
# Use -Dmaven.test.skip=true to skip both tests and compilation
RUN mvn package -Dmaven.test.skip=true

# Runtime stage
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port used by the application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 