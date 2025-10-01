# Java 17 + Maven
mvn -f park_management/pom.xml clean package
java -jar park_management/target/park_management-1.0-SNAPSHOT.jar
sudo apt update
sudo apt install -y postgresql postgresql-contrib

# start on boot + start now
sudo systemctl enable --now postgresql
sudo systemctl status postgresql
psql --version
# enter as the postgres superuser
sudo -u postgres psql
### set up 
cloudflared tunnel --url http://localhost:8080
# OpenAPI JSON
curl -I http://localhost:8080/v3/api-docs

# Swagger UI (some setups)
curl -I http://localhost:8080/swagger-ui/index.html
# or
curl -I http://localhost:8080/swagger-ui.html

git remote add origin https://github.com/tydeptrai21042004/parkmanage

run the test :mvn -f park_management/pom.xml -DskipTests=false test
