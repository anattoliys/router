FROM maven:3.8.4-openjdk-11 as build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk11:ubi
COPY --from=build /usr/src/app/target/router.jar /usr/app/router.jar
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "-Dserver.port=8082", "router.jar"]
