#
FROM maven:3.9.6-eclipse-temurin-21 AS deps
WORKDIR /build

#
COPY pom.xml .
COPY api/pom.xml api/pom.xml
COPY monolithic/pom.xml monolithic/pom.xml
COPY user/pom.xml user/pom.xml
COPY authuser/pom.xml authuser/pom.xml
COPY theme/pom.xml theme/pom.xml
COPY institution/pom.xml institution/pom.xml
COPY activity/pom.xml activity/pom.xml
COPY assessment/pom.xml assessment/pom.xml
COPY common/pom.xml common/pom.xml

#
RUN mvn -B -q -DskipTests -pl api -am dependency:go-offline

#
FROM deps AS build
WORKDIR /build
#
COPY . .
ARG PROFILE=dev
#
RUN mvn -B -DskipTests -P${PROFILE} -pl api -am package

#
FROM azul/zulu-openjdk-alpine:21
WORKDIR /app
#
COPY --from=build /build/api/target/*.jar /app/app.jar
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
