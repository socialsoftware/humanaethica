<h1 align="center">HumanaEthica</h1>

<p align="center">
  <a href="#about">About</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#installation">Installation</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

# About

**HumanaEthica** is an open-source broker that aims to connect charities and non-profit organizations with volunteers.

# Technologies

* Require download
  * [Postgres >= 14](https://www.postgresql.org/)
  * [Java 21](https://openjdk.org/projects/jdk/21/)
  * [Maven](https://maven.apache.org/download.cgi)
  * [Node 21.6](https://nodejs.org/en/) ([Node Version Manager](https://github.com/nvm-sh/nvm) recommended)
  * [Docker](https://www.docker.com/)
* No download required
  * [Spring-boot](https://spring.io/)
  * [Vue.js](https://vuejs.org/)

# Bare Metal Installation

* **Install**
```
sudo apt update && sudo apt upgrade
sudo apt install openjdk-21-jdk postgresql
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


* **Copy `frontend/example.env` to `frontend/.env` and uncomment for bare metal run**
* **Run Cypress Tests**
```
cd frontend
npx cypress run
```

* **Interactive Run of Cypress Tests**
```
cd frontend
npx cypress open
```
* **Run frontend**
```
cd frontend
npm i
npm start
```
* **Access http://localhost:8081**




The [following video](https://youtu.be/D0JABlXCdlo) shows how setup when you install the software in your machine. Requires the software mentioned above.

[![Watch the video](https://img.youtube.com/vi/D0JABlXCdlo/mqdefault.jpg)](https://youtu.be/D0JABlXCdlo)

# Docker Compose Installation

* Install [Docker](https://docs.docker.com/engine/install/) in you machine.

* Copy data/access.log.example to data/access.log
```
cp data/access.log.example data/access.log
```

* Copy data/access.log.example to data/access.log
```
cp data/error.log.example data/error.log
```

* Copy frontend/example.env to frontend/.env, and uncomment for docker compose up frontend
```
cp frontend/example.env frontend/.env
```

* Build HumanaEthica in project top directory, where docker-compose.yml is
```
docker compose build
```

* Run HumanaEthica
```
docker compose up -d frontend
```

* See container logs, for instance for backend
```
docker logs backend
```

* After changing backend code, for a quick recompile and restart
```
docker compose up --no-deps -d --build backend
```

* Shutdown HumanaEthica
```
docker compose down
```

* Run unit tests
* * All unit tests
```
docker compose up be-unit-tests 
```
* * A class of unit tests
```
UNIT=CreateEnrollmentMethodTest docker compose up be-unit-tests 
```
* * A unit test method
```
UNIT=CreateEnrollmentMethodTest#"create enrollment" docker compose up be-unit-tests 
```

* Run integration tests
* * All integration tests
```
docker compose up integration-tests
```
* * A class of integration tests
```
INTEGRATION=ValidateActivityWebServiceIT docker compose up integration-tests
```
* * An integration test method
```
INTEGRATION=ValidateActivityWebServiceIT#"volunteer validate activity" docker compose up integration-tests
```

* **Run Cypress Tests**
* uncomment frontend/.env for docker compose up e2e-run
```
docker compose up e2e-run
```

* **Open Cypress Tests**
* uncomment frontend/.env for docker compose up e2e-open
* Install X server
* * In OSX follow the [instructions](https://opencmiss-iron-tutorials.readthedocs.io/en/latest/faq/docker/setup_xquartz_x11_server_mac.html)
* * In Ubuntu
* * In WSL
* * In Windows
* Set environment variable DISPLAY=HOSTNAME:0, where HOSTNAME is obtained by 
```
ifconfig en0 | grep inet | awk '$1=="inet" {print $2}'
```
* Run docker
```
docker compose up e2e-open
```


# Development Container
An easy way to obtain a working development environment is to use the _development container_ provided (see folder `.devcontainer`). This requires [Docker](https://docs.docker.com/get-docker/).

The [following video](https://www.youtube.com/watch?v=ISNCrQ1r-Nw) shows how to setup the dev container using IDE IntelliJ IDEA (Ultimate Edition).

[![How to setup HumanaEthica in IntelliJ using a dev container](https://img.youtube.com/vi/ISNCrQ1r-Nw/mqdefault.jpg)](https://www.youtube.com/embed/ISNCrQ1r-Nw?si=1WeDwCsBdrr5OL5k)

# Contributing

Your contributions are always welcome!

# License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/socialsoftware/humanaethica/blob/master/LICENSE) file for details.
