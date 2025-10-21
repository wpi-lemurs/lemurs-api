#!/bin/bash

DBSTATUS=1
i=0

# Wait for PostgreSQL to be ready
while [[ "$DBSTATUS" != "0" && $i -lt 60 ]]; do
  sleep 1
  i=$((i+1))
  pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" > /dev/null 2>&1
  DBSTATUS=$?
done

if [[ "$DBSTATUS" != "0" ]]; then
  echo "PostgreSQL is taking more than 60 seconds to start up"
  exit 1
fi

/usr/config/starting-owner.sh

# Run 0000-initial-tables.sql first unconditionally
echo "Running initial tables update: 0000-initial-tables.sql"
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "/usr/config/updates/0000-initial-tables.sql"

# Initialize schema version file if it doesn't exist
if [ ! -e /var/lib/postgresql/data/schema.version ]; then
  echo "0000" > /var/lib/postgresql/data/schema.version
fi

current=$(cat /var/lib/postgresql/data/schema.version)

for entry in /usr/config/updates/*.sql; do
  filename=$(basename "$entry")
  index=${filename:0:4}
  test_tag=${filename:5:4}

  # Skip 0000 as we already ran it
  if [[ "$index" == "0000" ]]; then
    continue
  fi

  # String comparison is fine since we always use 4-digit zero-padded numbers
  if [[ "$index" > "$current" ]]; then
    if [[ "$test_tag" != "TEST" || "$POPULATE_TEST_DATA" = "Y" ]]; then
      echo "Running update: $index ($filename)"
      psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$entry"
    else
      echo "Skipping test update: $index ($filename)"
    fi
    current="$index"
  fi
done

echo "$current" > /var/lib/postgresql/data/schema.version
