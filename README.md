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

Run: `docker-compose up`