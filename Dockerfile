# Use lightweight JDK 17 base image
FROM openjdk:21-jdk-alpine  

# Set working directory inside the container
WORKDIR /app  

# Copy the packaged JAR from Maven target folder into the container
COPY target/khetsathi-0.0.1-SNAPSHOT.jar app.jar  

# Expose the app port (Spring Boot default: 8080)
EXPOSE 8080  

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
