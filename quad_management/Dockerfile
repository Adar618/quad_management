# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

# Build
COPY src ./src
# Produce an executable Spring Boot JAR
RUN mvn -q -B -DskipTests clean package spring-boot:repackage

# Make the jar name deterministic inside the image:
# Move the single Boot fat JAR (not the *-original.jar) to /app/app.jar
RUN set -e; \
    BOOT_JAR="$(ls -1 target/*.jar | grep -v 'original' | head -n 1)"; \
    echo "Boot JAR: ${BOOT_JAR}"; \
    cp "${BOOT_JAR}" /app/app.jar; \
    ls -al /app

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
# Copy the deterministic jar from build stage
COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080

# Optional: JVM ergonomics for containers
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]