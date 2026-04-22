FROM maven:3.9-eclipse-temurin-21 AS build
 
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
 
COPY src ./src
RUN mvn clean package -DskipTests -q
 
 
FROM eclipse-temurin:21-jre-jammy
 
WORKDIR /app
COPY --from=build /build/target/RiichiPointsCalculator-1.0.jar app.jar
COPY --from=build /build/src/main/resources/yaku.json src/main/resources/yaku.json
 
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]