# ==== Stage 1: Build ====
FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies first (better layer caching)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# ==== Stage 2: Run ====
FROM eclipse-temurin:21-jre

# create non-root user/group for security
    # Debian-based images use groupadd/useradd instead of Alpine's addgroup/adduser
    RUN groupadd -r spring && useradd -r -g spring spring
WORKDIR /app

# Copy the fat jar from the builder stage
COPY --from=builder /app/target/vulnguard-0.0.1-SNAPSHOT.jar app.jar

# ensure jar is owned by non-root user
RUN chown spring:spring /app/app.jar

# switch to the non-root user for runtime
USER spring:spring

# Expose application port
EXPOSE 8080

# Optional: JVM options hook
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]