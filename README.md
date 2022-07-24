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
  * [Postgres >= 10](https://www.postgresql.org/)
  * [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
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
sudo apt install openjdk-11-jdk postgresql
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


# Contributing

Your contributions are always welcome!

# License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/socialsoftware/humanaethica/blob/master/LICENSE) file for details.
