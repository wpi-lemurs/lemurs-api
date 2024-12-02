#!/bin/bash

if [ ! -e /var/opt/mssql/schema.version ]; then
  echo "-0001" > /var/lib/postgresql/schema.version
fi

/usr/config/update-schema.sh &

# Start PostgreSQL Server.
/usr/local/bin/docker-entrypoint.sh postgres