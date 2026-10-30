FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --uid 1001 spring
COPY --from=build /workspace/target/*.jar app.jar

USER spring
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
