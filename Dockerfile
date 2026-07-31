# build
FROM eclipse-temurin:21-jdk-jammy AS build

LABEL authors="namuk"

WORKDIR /build

COPY gradlew settings.gradle build.gradle ./

COPY gradle ./gradle

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar -x test

# run
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /build/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]