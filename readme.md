# Event Reservations REST API (Spring boot + Security JWT + Data JPA + Testing)
### Author: _RONALDO RODRIGUEZ_
## Table of Contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [Features](#features)
- [Run](#run-local)
- [Usage](#usage)
- [Testing](#testing)
---

### Introduction

The goal of this project is to develop a REST API for managing event reservations. The API will
allow users to register, authenticate, search for events, make reservations, and manage their
reservations. Additionally, it will include an administrator role for creating and managing events.
This project will incorporate business logic to handle event availability, reservation cancellations,
and user authentication.


---
### Features - Technical Requirements

**ORM with Spring Data JPA**: 
- Use Spring Data JPA for data persistence.
- Use a RDBMS like PostgreSQL or MySQL
- Model entities such as User, Event, and Reservation.

**Security**: 
- Implement authentication and authorization with JWT.
- Differentiate access based on role (Standard User vs. Administrator).

**API Documentation**: 
- Generate API documentation using OpenApi.
- Ensure the documentation is up-to-date and accessible.

**Unit Testing**: 
- Write unit tests for business logic.
- Use Mockito to mock dependencies.
---

### Architecture 

![WhatsApp Image 2025-01-09 at 10 09 39 PM](https://github.com/user-attachments/assets/aa1b785d-7a59-4a64-bb9f-5afc2208cf6a)

![WhatsApp Image 2025-01-09 at 10 09 23 PM](https://github.com/user-attachments/assets/726ba0d0-3c84-4cb2-afbf-fca78c728cf4)


---
### Steps to Run Locally
-  **In-Memory Profile ( Default )** : Uses H2 as the database.
- **Postgresql Profile** : Uses Postgresql as the database.



**1. First, clone the repository:**

    git clone <project>.git
    cd <project>

**2. Understanding In-Memory (Default) and Postgres**

- By default, the project uses the in-memory profile, which relies on an H2 database. No additional setup is needed.
- If you want to switch to the Postgres profile, you’ll need to:
  1. Update the active profile in the **application.properties** file.
  2. Set the PostgresSQL database credentials as environment variables.

I. Update the profile in **application.properties**
    
    
    #spring.profiles.active=in-memory
    spring.profiles.active=postgresql

II. Set the Postgres database credentials as environment variables

     PG_NAME_DB=<your-postgresql-database-name>
     PG_USERNAME_DB=<your-postgresql-username>
     PG_PASSWORD_DB=<your-postgresql-password>

**Finally, you can run the project** with `mvn spring-boot:run`
### Usage

---


### You can check the **API Swagger** on

    http://localhost:8080/swagger-ui/index.html#/Authentication

---
**Register a new user**

**Request**

	{
	"username": "string",
	"password": "string",
	"email": "string"
	}

POST /api/auth/register

    curl -i -H 'Accept: application/json' http://localhost:8080/api/auth/register

**Response**

    HTTP/1.1 201 Created
    OTHERS -> 400 Bad Request

	{
	"id": "number",
	"username":"string",
	"email": "string"
	}

 **Authenticate a user and return a token**

**Request**

	{
	"username": "string",
	"password": "string"
	}

POST /api/auth/login

    curl -i -H 'Accept: application/json' http://localhost:8080/api/auth/login

**Response**

    HTTP/1.1 200 Ok
    OTHERS -> 401 Unauthorized

	{
	"token":"string"
	}

 **List all available events**

GET /api/events

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events

**Response**

    HTTP/1.1 200 Ok
	[
		{ 
		 "id": "number", 
		 "name":"string", 
		 "description":"string",
		 "date":"string", 
		 "location":"string", 
		 "capacity":"number", 
		 "availability":"number"
		}
	]

 **Create a new event(Admin only)**

**Request**

	{
        "name": "string",
        "description": "string",
        "date": "string",
        "location": "string",
        "capacity": "number"
	}

POST /api/events

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events

**Response**

    HTTP/1.1 200 Ok
     OTHERS -> 400 Bad Request, 403 Forbidden

	{ 
        "id": "number", 
        "name":"string", 
        "description":"string",
        "date":"string", 
        "location":"string", 
        "capacity":"number", 
        "availability":"number"
	}

  
 **Get details of a specific event**

GET /api/events/{id} 

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events/{id}

**Response**

    HTTP/1.1 200 Ok
    OTHERS -> 404 Not Found

	{ 
        "id": "number", 
        "name":"string", 
        "description":"string",
        "date":"string", 
        "location":"string", 
        "capacity":"number", 
        "availability":"number"
	}

 **Update an existing event(Admin only)**
 
**Request**

	{
        "name": "string",
        "description": "string",
        "date": "string",
        "location": "string",
        "capacity": "number"
	}

PUT /api/events/{id} 

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events/{id}

**Response**

    HTTP/1.1 200 Ok
    OTHERS -> 409 Conflict, 403 Forbidden, 404 Not Found

	{ 
        "id": "number", 
        "name":"string", 
        "description":"string",
        "date":"string", 
        "location":"string", 
        "capacity":"number", 
        "availability":"number"
	}
 
 **Delete an event (Admin only)**
 
DELETE /api/events/{id} 

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events/{id}

**Response**

    HTTP/1.1 204 No Content
    OTHERS -> 403 Forbidden, 404 Not Found

 **Search for events by
name, date, or location**

**Request (Query Params)**

name=string&date=string&location=string
 
GET /api/events/search

    curl -i -H 'Accept: application/json' http://localhost:8080/api/events/search

**Response**

    HTTP/1.1 200 Ok
	[
        { 
            "id": "number", 
            "name":"string", 
            "description":"string",
            "date":"string", 
            "location":"string", 
            "capacity":"number", 
            "availability":"number"
        }
	]

**Make a reservation for an event**

**Request**

	{
	"eventId": "number",
	}

POST /api/reservations

    curl -i -H 'Accept: application/json' http://localhost:8080/api/reservations

**Response**

    HTTP/1.1 201 Created
     OTHERS -> 400 Bad Request, 409 Conflict

	{ 
        "id": "number", 
        "eventId": "number",
        "userId": "number",
        "status": "string",
	}

 **Cancel a reservation**
 
DELETE /api/reservations/{id}

    curl -i -H 'Accept: application/json' http://localhost:8080/api/reservations/{id}

**Response**

    HTTP/1.1 204 No Content
    OTHERS -> 403 Forbidden, 404 Not Found


 **List reservations for the logged-in user**

GET /api/reservations/user

    curl -i -H 'Accept: application/json' http://localhost:8080/api/reservations/user

**Response**

    HTTP/1.1 200 Ok
	[
		{ 
            "id": "number", 
            "eventId":"number", 
            "eventName":"string",
            "date":"string", 
            "status":"string"
		}
	]

 **List Attendees of a Specific Event**

GET /api/events/{id}/attendees


    curl -i -H 'Accept: application/json' http://localhost:8080/api/events/{id}/attendees

**Response**

    HTTP/1.1 200 Ok
    OTHERS -> 403 Forbidden, 404 Not Found
	[
		{ 
            "userId": "number", 
            "username":"string", 
            "email":"string",
            "reservationStatus":"string"
		}
	]

---

### Testing

**to run tests, try `mvn test`**

- Web Module - Integration Tests
- Core Module  - JUnit Tests

 
---

