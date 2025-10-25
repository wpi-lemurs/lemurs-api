# ==== Build Stage ====

# Use JDK 21 for gradlew.
FROM eclipse-temurin:21-jdk-alpine AS builder

# Cache layer for gradlew dependencies
WORKDIR /usr/app/
COPY gradlew *.gradle ./
COPY ./gradle ./gradle/
RUN ./gradlew dependencies

# Build the app.  This will build to /usr/app/build/libs/lemurs-api.jar
COPY . . 
RUN ./gradlew build -x check

# ==== Package Stage ====

# Use JRE 21.
FROM eclipse-temurin:21-jre-alpine

# Copy the built jar file.
RUN mkdir -p /opt/app
COPY --from=builder /usr/app/build/libs/lemurs-api.jar /opt/app/lemurs-api.jar

# Create the entrypoint.
ENTRYPOINT ["java", "-jar", "/opt/app/lemurs-api.jar"]