# kafka-consumer/Dockerfile

# --- STAGE 1: Build the metrics library ---
FROM maven:3.9.6-eclipse-temurin-17 AS library_builder
WORKDIR /app
COPY ./kafka-metrics-library/pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY ./kafka-metrics-library/src ./src
RUN mvn clean install

# --- STAGE 2: Build the consumer application ---
FROM maven:3.9.6-eclipse-temurin-17 AS consumer_builder
WORKDIR /app

# 1. FIRST, copy the local Maven repository from the library_builder stage.
#    This makes our internal library available before any other Maven command runs.
COPY --from=library_builder /root/.m2/repository /root/.m2/repository

# 2. NOW, copy the application's pom.xml and resolve its external dependencies.
COPY ./kafka-consumer/pom.xml ./pom.xml
RUN mvn dependency:go-offline

# 3. FINALLY, copy the source code and build the application.
COPY ./kafka-consumer/src ./src
RUN mvn clean package

# --- STAGE 3: Final runtime image ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=consumer_builder /app/target/kafka-consumer-*.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar", "app.jar"]