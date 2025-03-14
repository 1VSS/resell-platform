# Resell platform/marketplace API

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

# Resell Platform Frontend

This is the frontend application for the Resell Platform, a marketplace for buying and selling items.

## Features

- User authentication (login/register)
- Browse items in a feed
- Search for items
- Create, edit, and delete listings
- Purchase items

## Technologies Used

- Next.js 14 (App Router)
- TypeScript
- Tailwind CSS
- React Context API for state management

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

1. Clone the repository
2. Install dependencies:
   ```bash
   npm install
   ```
3. Create a `.env.local` file in the root directory with the following content:
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080
   ```
   (Adjust the URL if your backend is running on a different port)

### Running the Development Server

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser to see the application.

## Project Structure

- `src/app`: Next.js app router pages
- `src/components`: Reusable UI components
- `src/contexts`: React context providers
- `src/services`: API service functions
- `src/types`: TypeScript type definitions

## API Integration

The frontend communicates with the backend API through the service functions defined in `src/services/api.ts`. The API base URL is configured through the `NEXT_PUBLIC_API_URL` environment variable.

## Authentication

Authentication is handled using JWT tokens. When a user logs in or registers, the token is stored in localStorage and included in the Authorization header for protected API requests.

## Deployment

To build the application for production:

```bash
npm run build
```

To start the production server:

```bash
npm start
```

## Docker Setup

You can run the entire application stack (backend, frontend, and database) using Docker and Docker Compose without installing JDK, Node.js, or PostgreSQL locally.

### Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop/) installed on your machine
- [Docker Compose](https://docs.docker.com/compose/install/) (usually included with Docker Desktop)

### Running with Docker Compose

1. Clone the repository:

```bash
git clone https://github.com/1VSS/resell-platform.git
cd resell-platform
```

2. Start the application stack with Docker Compose:

```bash
docker-compose up -d
```

This will:
- Start a PostgreSQL database container
- Build and start the Spring Boot backend container
- Build and start the Next.js frontend container

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

4. To stop the application:

```bash
docker-compose down
```

### Development with Docker

If you want to make changes to the code while running in Docker:

1. For frontend development, you can mount your local directory:

```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up frontend
```

2. For backend development, rebuild the container after changes:

```bash
docker-compose up -d --build backend
```

### Troubleshooting

- If you encounter issues with the database connection, ensure that the PostgreSQL container is running:

```bash
docker ps | grep resell-db
```

- To view logs for any container:

```bash
docker logs resell-backend  # For backend logs
docker logs resell-frontend # For frontend logs
docker logs resell-db       # For database logs
```
