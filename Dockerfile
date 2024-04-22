FROM maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app
COPY . .
RUN mvn clean install

FROM eclipse-termurin:17.0.6_10-jdk
WORKDIR /app
COPY --from=build /app/target/app.jar /apps/
EXPOSE 8090
CMD ["java", "-jar", "app.jar"]