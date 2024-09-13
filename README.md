# LEMURS API

The API and database for the LEMURS project.

## Table of Contents

- [Links](#links)
- [Requirements](#requirements)
- [Setup](#setup)

## Links

- [Github](https://github.com/wpi-lemurs/lemurs-api)

## Requirements

- [Docker Compose](https://docs.docker.com/engine/install/)

## Setup

### Environment Variables

`LEMURS_POSTRGRES_HOST` - The database host url. Example: `localhost:5432` \
`LEMURS_POSTRGRES_USERNAME` - The database admin username. \
`LEMURS_POSTRGRES_PASSWORD` - The database admin password.

### Start

Run: `./gradlew build`
Copy the `lemurs-api-0.0.1-SNAPSHOT.jar` file to the docker folder.  Rename it to `lemurs-api`.
Run: `docker-compose up`