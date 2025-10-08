# Repository Structure

## root
> This is the root directory of the repository. It contains the main configuration files and directories for the project.
> 
> **Key Files:**
> - `README.md`: This file provides an overview of the project, including its purpose, setup instructions, and usage guidelines.
> - `LICENSE`: This file contains the licensing information for the project.
> - `docker-compose.yml`: This file is used to define and run multi-container Docker applications. It specifies the services, networks, and volumes for the application.
> - `.env`: This file contains environment variables used for configuration. It should not be committed to version control for security reasons.

## api
> This directory contains the backend code for the application. It is defined using Spring Boot, a popular framework for building Java-based web applications. The backend handles the core logic, data processing, and interactions with the database.:
> ### Key Files and Directories
> - `Dockerfile`: This file is used to build the Docker image for the backend service.
> - `src/`: This directory contains the source code for the backend application, including controllers, services, repositories, and models.
> - `/src/main/java/edu/wpi/lemurs/api/LemursApiApplication.java`: This is the main entry point for the Spring Boot application.
> - `src/main/java/edu/wpi/lemurs/api/endpoints/`: This directory contains the REST API endpoint definitions with .
> - `application.properties`: This file contains configuration settings for the Spring Boot application, such as database connection details and server settings.
> - `tests/`: This directory contains unit and integration tests for the backend application to ensure code quality and functionality.
> ### Setup and Usage
> 1. Ensure you have Java (JDK 17 or higher) installed on your machine.
> 2. To run the api locally, ensure you opened the api directory in your ide of choice (intellij is recommended) and run the `LemursApiApplication` file located in `src/main/java/edu/wpi/lemurs/api/`.
> 3. Assign a run configuration, pointing towards the .env file in the root directory of the repository.  See [IntelliJ environment variable setup guide](https://www.jetbrains.com/help/idea/program-arguments-and-environment-variables.html).
> 4. Go to an API testing platform of your choice (Postman is recommended) and send a GET request to `localhost:8080/health` to verify the server is running.
> 5. Continue testing your endpoints as needed.
> ## Notes
> - [Lemurs 2025-2026 API Notes](https://docs.google.com/document/d/1Oq1I40rgjFj1YD-g5AhjMZDqwHub3zn_XNthV2zHXeg/edit?usp=sharing)

## proxy
> This directory contains the configuration files for the Nginx reverse proxy server used in the Lemurs API project. The proxy server handles incoming HTTP requests and forwards them to the appropriate backend services, such as the API server or the web frontend.
> - Requests to `/api/` are forwarded to the backend API service (lemurs-api).
> - Requests to `/web/` are forwarded to the frontend web service (lemurs-web).
> - Requests to `/` are redirected to `/web/`.

## database
> This directory contains all resources related to the PostgreSQL database for the Lemurs API project. It includes schema definitions, migration scripts, and supporting files for database setup and maintenance.
> ### Connecting to the Database
> To connect to the PostgreSQL database, you can use a PostgreSQL client such as `DBeaver`. Here are the connection details:
> - **Host**: `130.215.43.64`
> - **Port**: `5432`
> - **Database Name**: `lemurs`
> - **Username**: Contact the backend team for the username
> - **Password**: Contact the backend team for the password
> ### Structure
> - **Dockerfile**: Builds the database container for local development and deployment.
> - **entrypoint.sh**: Entrypoint script for initializing the database container.
> - **starting-owner.sh**: Script for setting up the initial database owner and permissions.
> - **update-schema.sh**: Script to apply schema updates from the `updates/` folder to the database.
> - **updates/**: Contains ordered SQL migration files. Each file represents a change to the database schema (tables, columns, constraints, etc.).
>### Migration Workflow
> 1. **Schema Updates**: Place new migration scripts in the `updates/` folder, following the numeric naming convention (e.g., `0010-new-feature.sql`).
> 2. **Applying Updates**: Use `update-schema.sh` to apply all pending migrations to the database. This script ensures migrations are run in order.
> 3. **Initial Setup**: When starting from scratch, the container will run `entrypoint.sh` and `starting-owner.sh` to initialize the database and set up permissions.
> ## Dependencies
> - PostgreSQL (used as the database engine)
> - SQL scripts compatible with PostgreSQL
> - Docker (for containerized deployment)
> ## Notes
> - Migration scripts should be idempotent and ordered to prevent conflicts.
> - Review and test new migrations before applying to production.
> - Environment variables for database credentials should be managed securely and not committed to version control.
> ## Contact
> For questions or issues regarding the database setup, contact the backend development team.


## web
> Deployed at: https://lemurs-dev.wpi.edu/web
> 
>This directory contains the frontend code for the application. It's made with React and currently serves as a site where users are able to download an apk file to their Android devices. Administrators have a special endpoint `/admin` where they can manage users and assign roles. Functionality is being added by the 2025-2026 Lemurs MQP Dashboard team to provide a dashboard to view data collected from the Android app in a streamlined way.

## Working with the deployed version
> The deployed lemurs-api is hosted on a server at WPI. To work with this version, you will need to set up an SSH tunnel to securely connect to the server. Here are the steps to do so:
> 1. **Obtain Access**: Ensure you have the necessary permissions and credentials to access the WPI server. Contact the system administrator if you do not have access.
> 2. **Set Up SSH Key**: If you haven't already, generate an SSH key pair on your local machine using the command:
>    ```bash
>    ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
>    ```
>    Share your public key with the system administrator to add it to the server's authorized keys.
> 3. ssh lemurs-dev.wpi.edu to the server
> 
> ### Updating Specific Docker Containers on the Server
> 1. Ensure you are logged in as the `lemurs` user on the server. `su lemurs` (Contact the system admin for the password)
> 2. Navigate to this repository directory: `cd /opt/lemurs/lemurs-api`
> 3. `docker ps` to see the running containers
> 4. `docker stop <ID>` to stop a specific container
> 5. `docker rm <ID>` to remove a specific container
> 6. switch to whatever branch you need `git checkout <branch-name>`
> 7. `git pull` to get the latest changes on that branch
> 8. `docker compose up -d --build <service-name>`
> 9. `docker compose up -d --no-deps <service-name> `
> 
> ### Notes
> - [Lemurs 2025 - 2026 Docker Container Notes](https://docs.google.com/document/d/1mwNR2yMIgc1VMW6Z0VvXaVFCJR1UQDEwWutNlstt3Rs/edit?usp=sharing)