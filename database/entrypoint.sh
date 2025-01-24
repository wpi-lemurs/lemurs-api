#!/bin/bash

/usr/config/update-schema.sh &

# Start PostgreSQL Server.
/usr/local/bin/docker-entrypoint.sh postgres