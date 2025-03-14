# Authentication API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

This project is an API built using **Java, Java Spring, PostgresSQL as the database, and Spring Security and JWT for authentication and authorization control.**

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Database](#database)
- [TODO](#todo)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/1VSS/resell-platform.git
```

2. Install dependencies with Maven

3. Install [PostgresSQL](https://www.postgresql.org/)

## Usage

1. Start the application with Maven
2. The API will be accessible at http://localhost:8080/api/v1


## API Endpoints
The API provides the following endpoints:

```markdown
POST /register - Register a new user with username and password (no authentication required)

POST /authenticate - Authenticates the user. (requires authentication)

POST /items - Creates a new item listing (requires authentication)

PUT /items/id - Edits a listed item (requires authentication)

DELETE /items/id - Deletes a listed item (requires authentication)

POST /items/id/buy - Purchases a listed item (requires authentication)

GET /feed - Returns a feed with the listed items (no authentication required)
```

## Authentication
The API uses Spring Security for authentication and authorization control.
To access protected endpoints, provide the appropriate authentication credentials in the request header.

## Database
The project utilizes [PostgresSQL](https://www.postgresql.org/) as the database.

## TODO
- [ ] Add filters and search for the feed.
- [ ] Logging.
- [ ] Documentation with swagger.
