# ==============================================================================
# Multi-stage Dockerfile for Spring Boot Application
# Stage 1: Build the artifact using Maven and JDK 17
# Stage 2: Run the artifact in a lightweight JRE 17 Alpine image
# ==============================================================================

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Pre-fetch Maven dependencies to optimize Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and package executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security best practice: Run as a non-privileged system user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Document that container listens on port 8080
EXPOSE 8080

# Launch application
ENTRYPOINT ["java", "-jar", "app.jar"]
