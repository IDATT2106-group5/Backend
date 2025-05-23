# Backend for Krisefikser

This is the backend readme for the Krisefikser project.

## Environment Setup

Before running the application, you need to set up your environment variables in a .env file. Ask
the developer team for the file, then add it to the root directory of the project.

### Required Environment Variables

Look in the .env.example file that lies at the root of the project.

## Running the Application

### With Docker (Recommended)

If you have Docker installed on your machine, start Docker in a logged out mode and then run:

```sh
docker compose up --build
```

*Note: First build may take some time (approximately five minutes or more)*

To stop the container:

```sh
docker compose down
```

### Without Docker

To run the backend directly:

```sh
mvn clean install
mvn spring-boot:run
```

## Running Tests

To run the tests:

```sh
mvn clean test
```

For test coverage report:

```sh
mvn jacoco:report
```

The coverage report can be found at `target/site/jacoco/index.html` and can be opened in any
browser.

## Database Connection

To connect to the database, make sure you've properly configured the database URL, username, and
password in your .env file.

## Project Structure

The backend follows a standard Spring Boot application structure:

- krisefikser - Main source code
- resources - Application configuration
- test - Test cases

## Troubleshooting

If you encounter any issues:

1. Ensure your .env file is correctly configured
2. Check database connectivity
3. Verify you're using the correct Java version (17 or newer)
4. Make sure all required dependencies are installed

