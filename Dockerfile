FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom files to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source
COPY src ./src

# Build
RUN ./mvnw clean package -DskipTests

# ----------------------------

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]