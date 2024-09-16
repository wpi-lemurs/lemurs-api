# LEMURS API

The API and database for the LEMURS project.

## Table of Contents

- [Links](#links)
- [Requirements](#requirements)
- [Setup](#setup)
- [Quick Development](#quick-development)

## Links

- [Github](https://github.com/wpi-lemurs/lemurs-api)

## Requirements

- [Docker Compose](https://docs.docker.com/engine/install/)

## Setup

### Environment Variables

Copy `.env.example` to `.env` and edit variables as needed:

`LEMURS_POSTRGRES_USERNAME` - The database admin username. \
`LEMURS_POSTRGRES_PASSWORD` - The database admin password.

### Start

Run: `docker-compose up -d`

## Quick Development

When trying to quickly re-run the application, it may be preferable to run the api locally, instead of rebuilding the docker image.

To do this:
1. Install JDK 21
2. Add the following environment variable: \
`LEMURS_POSTRGRES_HOST` - The database host url. Default: `localhost:5432`
3. Shut off the api container. (But not the database container.)
4. Run `./gradlew bootRun` to start api locally.  