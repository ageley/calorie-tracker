FROM bellsoft/liberica-openjre-alpine:26.0.1-10
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
