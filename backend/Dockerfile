FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.10_7-jdk AS runtime
WORKDIR /app
COPY --from=build /app/attendance-service-logic/target/attendance-service-logic-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
