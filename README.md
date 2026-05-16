# Job Alert Bot

An automated job alert system built with Java and Spring Boot that fetches jobs from company career APIs, filters relevant opportunities, stores them in PostgreSQL, and sends email notifications using Mailgun.

## Features

- Fetches jobs from company career APIs
- Filters jobs based on title, location, experience, and skills
- Calculates a relevance score for each job
- Stores jobs in PostgreSQL and avoids duplicate notifications
- Sends email alerts for newly matched jobs
- Runs automatically on a configurable schedule
- Deployed on Render with Better Stack health monitoring

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Mailgun API
- Render

## Configuration

Update `application.properties` or `application-prod.properties` with your PostgreSQL and Mailgun credentials.

### PostgreSQL Configuration

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
```

### Mailgun Configuration

```properties
mailgun.api-key=<your-mailgun-api-key>
mailgun.domain=<your-mailgun-domain>
mailgun.from=Job Alert Bot <postmaster@your-domain.mailgun.org>
app.email.to=your-email@example.com
```

### Job Search Criteria

```properties
app.criteria.titles=Software Engineer,Senior Software Engineer,SDE 2
app.criteria.locations=Bengaluru,Hyderabad
app.criteria.min-experience=4
app.criteria.max-experience=6
app.criteria.required-skills=Java,Spring Boot,Microservices
app.criteria.excluded-keywords=intern,contract
app.criteria.minimum-score=70
```

### Scheduler Configuration

```properties
app.scheduler.cron=0 0 */2 * * *
```

Runs every 2 hours (configured with `Asia/Kolkata` timezone).

## REST Endpoints

### Health Check

```http
GET /health
```

### Run Job Search Manually

```http
POST /jobs/run
```

## Run Locally

```bash
./mvnw spring-boot:run
```

Trigger the job search manually:

```bash
curl -X POST http://localhost:8080/jobs/run
```

## Deployment

The application is deployed on Render.
