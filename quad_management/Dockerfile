# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

# Build
COPY src ./src
# Use spring-boot:repackage to ensure executable JAR
RUN mvn -q -B -DskipTests clean package spring-boot:repackage

# Find the executable JAR (not the -original.jar)
RUN JAR_FILE=$(find target -name "*.jar" ! -name "*-original.jar" | head -n 1) && \
    echo "Found JAR: $JAR_FILE" && \
    cp "$JAR_FILE" /app/app.jar && \
    ls -lh /app/app.jar

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the executable jar from build stage
COPY --from=build /app/app.jar /app/app.jar

# Verify the JAR is executable
RUN jar -tf /app/app.jar | grep -q "BOOT-INF" || (echo "ERROR: Not a Spring Boot JAR" && exit 1)

EXPOSE 8080

# JVM ergonomics for containers
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]