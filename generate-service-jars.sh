cd backend
mvn clean package -Dmaven.test.skip=true
cd ..
docker compose build
docker compose up -d