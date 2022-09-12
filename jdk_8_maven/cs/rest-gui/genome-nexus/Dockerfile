FROM maven:3.6-jdk-13
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
RUN mvn -DskipTests clean install

FROM openjdk:13-slim
COPY --from=0 /genome-nexus/web/target/*.war /app.war
CMD ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
