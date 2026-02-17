FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy the entire project context (gradlew, build.gradle.kts, settings.gradle.kts, src, etc.)
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test bootJar --no-daemon


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the application jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]