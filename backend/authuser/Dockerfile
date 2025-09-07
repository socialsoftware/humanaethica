FROM azul/zulu-openjdk-alpine:21
WORKDIR /app
COPY target/authuser-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8083
CMD ["java","-Dspring.profiles.active=dev","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]