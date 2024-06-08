FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
EXPOSE 8080
ADD target/weather-aggregator.jar weather-aggregator.jar
ENTRYPOINT ["java", "-jar", "/weather-aggregator.jar"]