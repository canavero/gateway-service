FROM eclipse-temurin:25-jre
VOLUME /tmp
COPY target/*.jar /app.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app.jar"]
