cd backend
mvn clean package
cd ..
docker compose build
docker compose up -d