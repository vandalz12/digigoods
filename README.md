# Digigoods API

Digigoods is the backend API for a web marketplace for purchasing digital goods.

## Features

The main feature of this API is simulating the use case when customer purchasing digital goods with discounts.
A user authenticates first to obtain the JWT token, then use the token to call the checkout endpoint to create an order.

The other features present in the API are:

- Get list of products available in the marketplace
- Get list of discounts available in the marketplace

## Getting Started

Before you begin, make sure you have the following installed:

- **Java 21** or higher
- **PostgreSQL 15** or higher
- **Docker** and **Docker Compose**, if you prefer to build and run the app as containers
- **Git**

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd digigoods
```

### Step 2: Build the Project

The project uses Maven Wrapper, so you don't need to install Maven separately:

```bash
# On Linux/macOS
./mvnw clean compile

# On Windows
mvnw.cmd clean compile
```

### Step 3: Run Tests

To ensure everything is working correctly, run the test suite:

```bash
# On Linux/macOS
./mvnw test

# On Windows
mvnw.cmd test
```

You can check the coverage report at [`target/site/jacoco/index.html`](./target/site/jacoco/index.html).
Open the file in browser to view the report.

Alternatively, you can also use [Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters) extension to view the coverage report of a source code file directly in the editor.
Open a source code file, then right click on the editor and select `Coverage Gutters: Display Coverage`. The covered lines will have green highlights and the uncovered lines will have red highlights.

### Step 4: Start the Application

The easiest way to run the application is using Docker Compose, which will start both the PostgreSQL database and the application:

```bash
docker-compose up --build
```

This will:

- Start a PostgreSQL database on port 5432
- Build and start the Digigoods API on port 8080
- Automatically run database migrations with sample data

### Step 5: Verify the Application

Once the application is running, you can verify it's working by:

1. **Health Check**: Visit http://localhost:8080/actuator/health

### Alternative: Running Without Docker

If you prefer to run without Docker:

1. **Start PostgreSQL manually** (ensure it's running on `localhost:5432`)
2. **Create database**: `digigoods` with user `digigoods` and password `digigoods`
3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

You can adjust the database connection settings in [`src/main/resources/application.properties`](./src/main/resources/application.properties).

### Sample API Usage

After the application starts, you can test the API using `curl` or Postman:

1. **Login to get JWT token**:
   ```bash
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "testuser", "password": "password"}'
   ```

2. **Get products**:
   ```bash
   curl http://localhost:8080/products
   ```

3. **Create an order** (replace `<JWT_TOKEN>` with the token from step 1):
   ```bash
   curl -X POST http://localhost:8080/orders \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT_TOKEN>" \
     -d '{"productIds": [1, 2], "discountCodes": ["SUMMER20"]}'
   ```

### Stopping the Application

To stop the Docker containers:

```bash
docker-compose down
```

To remove all data (including database):

```bash
docker-compose down -v
```

## Hands-on Instructions

See [`HANDS-ON.md`](./HANDS-ON.md).

## AI Assistance Disclosure

[Gemini 2.5 Pro API](https://cloud.google.com/vertex-ai/generative-ai/docs/models/gemini/2-5-pro) was used to help brainstorm and refine the requirements, while [Augment Code](https://www.augmentcode.com/) served as an AI chat assistant and agent during development of the project. The requirements are documented in [`docs/PROMPT.md`](./docs/PROMPT.md).

## License

This project is licensed under the [MIT License](./LICENSE.md).
