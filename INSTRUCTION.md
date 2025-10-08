# Repository Structure

## Root
This is the root directory of the repository. It contains the main configuration files and directories for the project.

**Key Files:**
- `README.md`: Overview of the project, setup instructions, and usage guidelines.
- `LICENSE`: Licensing information for the project.
- `docker-compose.yaml`: Defines and runs multi-container Docker applications, specifying services, networks, and volumes.
- `.env`: Contains environment variables for configuration. Should not be committed to version control for security reasons.

## api
This directory contains the backend code for the application, built with Spring Boot. The backend handles core logic, data processing, and database interactions.

**Key Files and Directories:**
- `Dockerfile`: Builds the Docker image for the backend service.
- `src/`: Source code for the backend application (controllers, services, repositories, models).
- `src/main/java/edu/wpi/lemurs/api/LemursApiApplication.java`: Main entry point for the Spring Boot application.
- `src/main/java/edu/wpi/lemurs/api/endpoints/`: REST API endpoint definitions (controller classes).
- `application.properties`: Configuration settings (database connection, server settings, etc.).
- `tests/`: Unit and integration tests for backend code quality and functionality.

**Setup and Usage:**
1. Ensure Java (JDK 17+) is installed.
2. Open the `api` directory in your IDE (IntelliJ recommended) and run `LemursApiApplication`.
3. Assign a run configuration, pointing to the `.env` file in the root directory. See [IntelliJ environment variable setup guide](https://www.jetbrains.com/help/idea/program-arguments-and-environment-variables.html).
4. Use an API testing platform (e.g., Postman) to send a GET request to `localhost:8080/health` to verify the server is running.
5. Continue testing endpoints as needed.

**Notes:**
- [Lemurs 2025-2026 API Notes](https://docs.google.com/document/d/1Oq1I40rgjFj1YD-g5AhjMZDqwHub3zn_XNthV2zHXeg/edit?usp=sharing)

## proxy
This directory contains configuration files for the Nginx reverse proxy server. The proxy handles incoming HTTP requests and forwards them to backend services (API server or web frontend).

- Requests to `/api/` are forwarded to the backend API service (lemurs-api).
- Requests to `/web/` are forwarded to the frontend web service (lemurs-web).
- Requests to `/` are redirected to `/web/`.

## database
This directory contains all resources related to the PostgreSQL database, including schema definitions, migration scripts, and supporting files for setup and maintenance.

**Connecting to the Database:**
Use a PostgreSQL client (e.g., DBeaver) with the following details:
- **Host:** `130.215.43.64`
- **Port:** `5432`
- **Database Name:** `lemurs`
- **Username/Password:** Contact the backend team

**Structure:**
- `Dockerfile`: Builds the database container for development and deployment.
- `entrypoint.sh`: Initializes the database container.
- `starting-owner.sh`: Sets up the initial database owner and permissions.
- `update-schema.sh`: Applies schema updates from the `updates/` folder.
- `updates/`: Ordered SQL migration files (tables, columns, constraints, etc.).

**Migration Workflow:**
1. Place new migration scripts in `updates/`, following numeric naming (e.g., `0010-new-feature.sql`).
2. Use `update-schema.sh` to apply pending migrations in order.
3. For initial setup, the container runs `entrypoint.sh` and `starting-owner.sh`.

**Dependencies:**
- PostgreSQL
- SQL scripts compatible with PostgreSQL
- Docker

**Notes:**
- Migration scripts should be idempotent and ordered.
- Review and test migrations before production.
- Manage database credentials securely.

**Contact:**
For database setup questions/issues, contact the backend development team.

## web
Deployed at: https://lemurs-dev.wpi.edu/web

This directory contains the frontend code, built with React. It serves as a site for users to download the Android APK. Administrators can manage users and assign roles at `/admin`. The dashboard is being developed to visualize app data.

## Working with the Deployed Version
The deployed lemurs-api is hosted on a WPI server. To work with it, set up an SSH tunnel:

1. **Obtain Access:** Ensure you have permissions and credentials. Contact the system administrator if needed.
2. **Set Up SSH Key:** Generate an SSH key pair:
   ```bash
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```
   Share your public key with the administrator.
3. SSH to the server: `ssh lemurs-dev.wpi.edu`

**Updating Specific Docker Containers:**
1. Log in as the `lemurs` user: `su lemurs` (ask admin for password)
2. Navigate to repo: `cd /opt/lemurs/lemurs-api`
3. List containers: `docker ps`
4. Stop container: `docker stop <ID>`
5. Remove container: `docker rm <ID>`
6. Switch branch: `git checkout <branch-name>`
7. Pull latest: `git pull`
8. Rebuild service: `docker compose up -d --build <service-name>`
9. Restart service: `docker compose up -d --no-deps <service-name>`

**Notes:**
- [Lemurs 2025-2026 Docker Container Notes](https://docs.google.com/document/d/1mwNR2yMIgc1VMW6Z0VvXaVFCJR1UQDEwWutNlstt3Rs/edit?usp=sharing)
