# Build stage
FROM maven:3.9-amazoncorretto-21

# Set working directory
WORKDIR /app

# Copy the project
COPY . .

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run all dependencies without requiring an internet connection
RUN mvn dependency:go-offline

# Default command uses Spring Boot's dev mode
CMD ["mvn", "spring-boot:run", "-DskipTests", "-Dspring-boot.run.jvmArguments=-XX:TieredStopAtLevel=1 -Dspring.devtools.restart.enabled=true -Dspring.output.ansi.enabled=always"]