# LEMURS API

The API and database for the LEMURS project.

## Table of Contents

- [Links](#links)
- [Requirements](#requirements)
- [Tools](#tools)
- [Setup](#setup)
- [Run](#run)
- [Quick Development](#quick-development)

## Links

- [Github](https://github.com/wpi-lemurs/lemurs-api)

## Requirements

- Docker - [Download](https://docs.docker.com/engine/install/)

## Tools

- DBeaver - [Download](https://dbeaver.io/download/) \
  Useful for connecting to the database directly. Locally, the database should be a Postgres database at `localhost:5432`.  The database name is `lemurs`.

- Postman - [Download](https://www.postman.com/downloads/) \
  Useful for testing API endpoints.

## Setup

### Environment Variables

Copy `.env.example` to `.env` and edit variables as needed:

`COMPOSE_PROFILES` - Which environment this is for. Enumeration: `dev`, `dev-partial`, `prod`
`LEMURS_POSTRGRES_USERNAME` - The database admin username. \
`LEMURS_POSTRGRES_PASSWORD` - The database admin password. \
`LEMURS_API_PORT` - The api port that will be exposed. Example: `8080`

## Run

Run: `docker-compose up -d` \
Stop: `docker-compose down`

Stop and remove the image: Run compose down and add a `--rmi local` flag.

> [!WARNING]  
> The followng deletes data! \
> Stop and remove volumes: Run compose down and add a `-v` flag.


## Quick Development

When trying to quickly re-run the application, it may be preferable to run the api locally, instead of rebuilding the docker image.

To do this:
1. Install JDK 21
2. Add the following environment variable: \
`LEMURS_POSTRGRES_HOST` - The database host url. If database is run locally, this should be: `localhost:5432`
3. Use `COMPOSE_PROFILES = "dev-partial"` for the environment.
4. Run `./gradlew bootRun` to start api locally.  