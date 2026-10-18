
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/pod-portal.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]
