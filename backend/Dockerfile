FROM eclipse-temurin:latest
COPY target/RiichiPointsCalculator-1.0.jar app.jar
COPY src/main/resources/yaku.json src/main/resources/yaku.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]