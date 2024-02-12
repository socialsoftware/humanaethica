<h1 align="center">Humana Ética</h1>

<p align="center">
  <a href="#about">About</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#installation">Installation</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

# About

**HumanaEthica** ...

# Technologies

* Require download
  * [Postgres >= 14](https://www.postgresql.org/)
  * [Java 17](https://openjdk.org/projects/jdk/17/)
  * [Maven](https://maven.apache.org/download.cgi)
  * [Node 16.14](https://nodejs.org/en/) ([Node Version Manager](https://github.com/nvm-sh/nvm) recommended)
  * [Docker](https://www.docker.com/)
* No download required
  * [Spring-boot](https://spring.io/)
  * [Vue.js](https://vuejs.org/)

# Installation

* **Install**
```
sudo apt update && sudo apt upgrade
sudo apt install openjdk-17-jdk postgresql
```
* **Start db, change to postgres user and create DB**
```
sudo service postgresql start
sudo su -l postgres
dropdb hedb
createdb hedb
```
* **Create user to access db**
```
psql hedb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
\q
exit
```
* **Rename `backend/src/main/resources/application-dev.properties.example` to `application-dev.properties` and fill its fields**
* **Run server**
```
cd backend
mvn clean spring-boot:run
```
* **See documentation on http://localhost:8080/swagger-ui.html**
* **Rename `frontend/example.env` to `.env` and fill its fields**
* **Run frontend**
```
cd frontend
npm i
npm start
```

* **Access http://localhost:8081**

The [following video](https://youtu.be/D0JABlXCdlo) shows how setup when you install the software in your machine. Requires the software mentioned above.

[![Watch the video](https://img.youtube.com/vi/D0JABlXCdlo/mqdefault.jpg)](https://youtu.be/D0JABlXCdlo)

# Development Container
An easy way to obtain a working development environment is to use the _development container_ provided (see folder `.devcontainer`). This requires [Docker](https://docs.docker.com/get-docker/).

The [following video](https://www.youtube.com/watch?v=ISNCrQ1r-Nw) shows how to setup the dev container using IDE IntelliJ IDEA (Ultimate Edition).

[![How to setup HumanaEthica in IntelliJ using a dev container](https://i9.ytimg.com/vi_webp/ISNCrQ1r-Nw/mq2.webp?sqp=CJCdqa4G-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGEYgVShyMA8=&rs=AOn4CLAVQ3NVAYVa08_qFJmu2xSqyOCUIw)](https://www.youtube.com/embed/ISNCrQ1r-Nw?si=1WeDwCsBdrr5OL5k)

# Contributing

Your contributions are always welcome!

# License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/socialsoftware/humanaethica/blob/master/LICENSE) file for details.
