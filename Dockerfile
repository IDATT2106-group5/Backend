# Build stage
FROM maven:3.9-amazoncorretto-21

# Set working directory
WORKDIR /src

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Copy pom-file
COPY pom.xml .

# Run all dependencies without requiring a internet-connection
RUN mvn dependency:go-offline

# Default command uses Spring Boot's dev mode
CMD ["./mvnw", "spring-boot:run", "-DskipTests", "-Dspring-boot.run.jvmArguments='-XX:TieredStopAtLevel=1 -Dspring.devtools.restart.enabled=true -Dspring.output.ansi.enabled=always'"]