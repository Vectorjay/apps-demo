FROM eclipse-temurin:21
WORKDIR /app
COPY target/demo-*.jar app.jar
EXPOSE 8080
CMD java -jar demo-*.jar
