# Use a base image that includes JDK 21
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /Users/adityakaushik_local/Downloads/food-ordering-service-master

# Copy the JAR file from the target directory into the container
COPY target/food-ordering-service-1.0-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
