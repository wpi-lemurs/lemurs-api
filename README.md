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
`LEMURS_POSTGRES_USERNAME` - The database admin username. \
`LEMURS_POSTGRES_PASSWORD` - The database admin password. \
`LEMURS_API_PORT` - The api port that will be exposed. Example: `8080`
`LEMURS_SIGNATURE` - A base 64 url encoded jwt secret. Can be generated with tools like: https://jwtsecret.com/generate.\
`LEMURS_EMAIL_ADDRESS` - The email alias that will be used for sending the email. Example `lemurs-noreply@wpi.edu` \
`LEMURS_EMAIL_HOST` - The smtp email host server. Example: `smtp-mail.outlook.com` \
`LEMURS_EMAIL_PORT` - The smtp port. Example: `587` \
`LEMURS_EMAIL_USERNAME` - The email account login username. Ideally this should be a service account. Example: `lemurs-noreply@wpi.edu` \
`LEMURS_EMAIL_PASSWORD` - The email account's password or app key.

Note that in production configuration, the database username/password should not matter a lot as the database is not exposed.
However, the jwt secret in LEMURS_SIGNATURE can be used to take full control of the API.

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
2. Add all of the environment variables to your user/system environment variables.  (Spring will not be able to use the .env file)
3. Add the following environment variable: \
`LEMURS_POSTGRES_HOST` - The database host url. If database is run locally, this should be: `localhost:5432`
4. Use `COMPOSE_PROFILES = "dev-partial"` for the environment.
5. Run `./gradlew bootRun` to start api locally.  