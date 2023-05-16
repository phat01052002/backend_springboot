#
# Build stage
#
FROM maven:3.8.2-jdk-19 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:19-alpine
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
# ENV PORT=3000
EXPOSE 3000
ENTRYPOINT ["java","-jar","demo.jar"]