# TicketHub: Online Ticket Booking REST API

A Spring Boot REST API for booking and cancelling event tickets, backed by MySQL, containerized with Docker and deployable to **Azure App Service** with **Azure Database for MySQL** and a **GitHub Actions** CI/CD pipeline.

## Features
- Book a ticket for an event seat (`POST /tickets`)
- List tickets, optionally filtered by event (`GET /tickets?eventName=Concert`)
- Get one ticket (`GET /tickets/{id}`)
- Cancel a ticket (`DELETE /tickets/{id}`), after which the seat can be booked again
- Double-booking prevention: the seat check and save run in one transaction
- Optimistic locking (`@Version`) to detect concurrent updates to the same ticket
- Input validation and a global exception handler with clear JSON errors (400, 404, 409)
- Swagger UI for API docs at `/swagger-ui.html`
- JUnit 5 + Mockito unit tests

## Tech stack
Java 17, Spring Boot 3, Spring Data JPA (Hibernate), MySQL 8, Maven, Docker, GitHub Actions, Azure App Service, Azure Database for MySQL

## Architecture
```
Client (Postman / Swagger)
        |
        v
Azure App Service (Spring Boot JAR, Java 17)
        |
        v
Azure Database for MySQL (Flexible Server)

GitHub push to main --> GitHub Actions (test + build) --> deploy to App Service
```

## Run locally

### Option 1: Docker Compose (easiest)
```bash
docker compose up --build
```
App runs at http://localhost:8080 and Swagger at http://localhost:8080/swagger-ui.html

### Option 2: Maven + MySQL
```bash
docker run --name mysql -e MYSQL_ROOT_PASSWORD=pass -e MYSQL_DATABASE=tickethub -p 3306:3306 -d mysql:8
mvn spring-boot:run
```

Database settings are read from environment variables (defaults are for local use only):
`DB_URL`, `DB_USER`, `DB_PASS`

## Run tests
```bash
mvn test
```

## API examples
Book a ticket:
```bash
curl -X POST http://localhost:8080/tickets \
  -H "Content-Type: application/json" \
  -d '{"eventName":"Concert","seatNumber":"A1","customerName":"Navya"}'
```
Booking the same seat again returns `409 Conflict`.

List tickets: `curl http://localhost:8080/tickets`
Cancel ticket 1: `curl -X DELETE http://localhost:8080/tickets/1`

## Deploy to Azure

```bash
az login
az group create -n tickethub-rg -l centralindia

# MySQL database
az mysql flexible-server create -g tickethub-rg -n tickethub-db-<yourname> \
  --admin-user dbadmin --admin-password '<StrongPass1!>' \
  --sku-name Standard_B1ms --tier Burstable --public-access 0.0.0.0
az mysql flexible-server db create -g tickethub-rg -s tickethub-db-<yourname> -d tickethub

# App Service
az appservice plan create -g tickethub-rg -n tickethub-plan --is-linux --sku B1
az webapp create -g tickethub-rg -p tickethub-plan -n tickethub-<yourname> --runtime "JAVA:17-java17"

# Configuration (credentials stay out of the code)
az webapp config appsettings set -g tickethub-rg -n tickethub-<yourname> --settings \
  DB_URL="jdbc:mysql://tickethub-db-<yourname>.mysql.database.azure.com:3306/tickethub?useSSL=true" \
  DB_USER=dbadmin DB_PASS='<StrongPass1!>' WEBSITES_PORT=8080

# Build and deploy
mvn clean package
az webapp deploy -g tickethub-rg -n tickethub-<yourname> --src-path target/tickethub.jar --type jar
```

Live URL: `https://tickethub-<yourname>.azurewebsites.net/swagger-ui.html`

### CI/CD with GitHub Actions
The workflow in `.github/workflows/azure-deploy.yml` runs tests, builds the JAR and deploys on every push to `main`. Add these repository secrets:
- `AZURE_WEBAPP_NAME`: your App Service name
- `AZURE_WEBAPP_PUBLISH_PROFILE`: the publish profile downloaded from the App Service overview page

### Clean up to avoid charges
```bash
az group delete -n tickethub-rg
```

## Screenshots
Add your Swagger and live deployment screenshots here.

## Author
Navya Boya | [LinkedIn](https://www.linkedin.com/in/boya-navya-587288299) | [GitHub](https://github.com/Navyaboya)
