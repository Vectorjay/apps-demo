FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/demo-*.jar app.jar
EXPOSE 8080
//ENTRYPOINT ["java", "-jar", "app.jar"]
CMD java -jar demo-*.jar
