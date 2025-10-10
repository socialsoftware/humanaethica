Write-Output "Maven build (sem testes) dos módulos backend..."
#cd backend
#mvn clean package
#cd ..

Write-Output "Build das imagens Docker..."
docker build -t humanaethica-eureka:test      backend/eureka_server
docker build -t humanaethica-monolithic:test  backend/monolithic
docker build -t humanaethica-authuser:test    backend/authuser
docker build -t humanaethica-api:test         backend/api

Write-Output " Executar E2E (Testcontainers + compose de backend/e2e)..."
cd backend/e2e
mvn -q -Ptest-int verify
cd ../..