FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

ENV MAVEN_OPTS="-Xmx350m -Xms256m -XX:MaxMetaspaceSize=128m"

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx350m", "-jar", "app.jar"]