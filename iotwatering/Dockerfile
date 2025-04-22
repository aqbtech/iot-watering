FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the POM file first to leverage Docker cache for dependencies
COPY pom.xml .
# Download all dependencies. Dependencies will be cached unless pom.xml changes
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application, skipping tests
RUN mvn clean package -DskipTests

# Create a smaller runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
