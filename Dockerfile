# Use a lightweight OpenJDK image
FROM openjdk:17-jdk-slim

# Set a working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
