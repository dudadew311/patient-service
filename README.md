# Patient Management Microservice

A Spring Boot REST API built to handle patient registration and clinical management records. This service leverages PostgreSQL for persistent storage and uses Flyway to manage relational schema migrations seamlessly.

## Tech Stack
* **Framework:** Spring Boot 3.2.5
* **Language:** Java 17 (Amazon Corretto)
* **Database:** PostgreSQL 15
* **Migration Tool:** Flyway Migration Engine
* **Containerization:** Docker Compose, Testcontainers
* **Utilities:** Lombok, Jakarta Validation

---

## Prerequisites
Before running this application, ensure you have the following installed:
* **Java 17** SDK
* **Maven 3.x+**
* **Docker Desktop**

---
## Interactive API Documentation
When the application is running locally, you can view and test the complete interactive REST API documentation via Swagger UI at:
* **URL:** http://localhost:8080/swagger-ui/index.html

## Security & Access Control
The microservice is secured using HTTP Basic Authentication. Access to endpoints depends on the assigned user roles:

* **Public Access (`.permitAll()`)**:
  * Swagger UI / OpenAPI documentation.
  * `GET /api/v1/patients` (Read-only access to patient lists).
* **Admin Access (`hasRole('ADMIN')`)**:
  * Required for all mutating actions (`POST`, `PUT`, `DELETE`).

### Test Credentials (In-Memory)
For testing purposes, two mock users are configured:
* **Admin User**: Username: `admin` | Password: `admin123` (Full write privileges)
* **Staff User**: Username: `staff` | Password: `staff123` (Read-only privileges)

## Local Development Setup

### 1. Start the Database Container
The application relies on a localized PostgreSQL environment. Spin up the infrastructure container using Docker Compose:
```bash
docker compose up -d
```

On initialization, Flyway will automatically scan the src/main/resources/db/migration/ folder and execute any pending schema updates (such as V1__init_schema.sql).

### 2. Run the Application
Compile the source code and start the embedded Tomcat container using Maven:
```bash
mvn clean spring-boot:run
```

---

## Core API Endpoints

### 1. Register a Patient
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/v1/patients`
* **Payload Format (JSON):**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "johndoe@example.com",
  "dateOfBirth": "1990-05-15"
}
```
### 2. Update Patient Details
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/v1/patients/{id}`
* **Payload Format (JSON)**:
```json
{
  "firstName": "Tom",
  "lastName": "Doe",
  "email": "johndoe@example.com",
  "dateOfBirth": "1990-05-15"
}
```

### 3. Delete a Patient
* **Method:** `DELETE`
* **URL:** `http://localhost:8080/api/v1/patients/{id}`
* **Payload Format:** None (Flags record as deleted via `is_deleted` column and returns a `204 No Content` status code on success)

### 4. Get Paginated Patients
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/v1/patients`
* **Query Parameters (Optional):**
    * `lastName` (String): Filter patients by a partial, case-insensitive match on their last name.
    * `page` (Integer, default: `0`): The page index to retrieve.
    * `size` (Integer, default: `10`): Number of records per page.
    * `sort` (String, default: `id,asc`): Target sorting field and direction (e.g., `lastName,desc`).
* **Response Format (JSON Paginated Wrapper):**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "johndoe@example.com",
      "dateOfBirth": "1990-05-15",
      "createdAt": "2026-05-23T14:58:27",
      "updatedAt": "2026-05-23T15:02:11"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0
}
```
---

### Database Schema Features
* **Auditing:** Automatically captures record lifecycle states using `created_at` and `updated_at` timestamps.
* **Soft Deletes:** Records are safely flagged via an `is_deleted` column to preserve historical data integrity instead of performing hard database removal.

---

## Error Handling Standards
The system implements a standardized structural schema for client-facing failures. Input violations will yield an explicit `400 Bad Request` explaining the breakdown:
```json
{
    "timestamp": "2026-05-22T14:22:10.499429",
    "message": "Validation Failed",
    "details": "email: Invalid email format"
}
```
## Testing

The project includes both unit and integration tests. The integration tests are completely self-contained and do not require a manually running database instance.

* **Unit Tests:** Mock the service and repository layers using Mockito.
* **Integration Tests:** Use **Testcontainers** to automatically spin up a disposable PostgreSQL Docker container during the test lifecycle.

### Running the Tests

To run the entire test suite, simply execute:

```bash
mvn verify
```