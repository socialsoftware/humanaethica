FROM azul/zulu-openjdk-alpine:21
WORKDIR /app
COPY target/eureka_server-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8761
CMD ["java","-Dspring.profiles.active=prod","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]