# Stage 1: Build the JAR file using Maven
FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder

# Set the working directory inside the builder
WORKDIR /app

# Copy Maven project files into the container
COPY pom.xml .
COPY src ./src

# Run Maven to build the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image using a slim JDK
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage (correcting the stage name)
COPY --from=builder /app/target/Hugverk1-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application listens on
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]