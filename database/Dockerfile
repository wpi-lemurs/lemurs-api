FROM postgres:alpine

# Use the official Postgres init mechanism:
# Any *.sql or *.sh placed in /docker-entrypoint-initdb.d will run exactly once
# when the database is first created (i.e., when the data directory is empty).

# Copy schema and seed SQL files in lexical order.
# NOTE: We intentionally DO NOT copy 0004-starting-owner.sql because it's a placeholder
# that is generated dynamically from the environment (see the shell script below).
COPY updates/0000-initial-tables.sql          /docker-entrypoint-initdb.d/
COPY updates/0001-surveys.sql                 /docker-entrypoint-initdb.d/
COPY updates/0002-survey-data.sql             /docker-entrypoint-initdb.d/
COPY updates/0003-additional-tables.sql       /docker-entrypoint-initdb.d/
COPY updates/0005-demographics.sql            /docker-entrypoint-initdb.d/
COPY updates/0006-danger-alert-triggers.sql   /docker-entrypoint-initdb.d/

# Create a runtime init script to insert the starting owner using the env var.
# This runs after tables are created and will be skipped if LEMURS_OWNER_EMAIL is not set.
RUN set -eux; \
    cat > /docker-entrypoint-initdb.d/0004-starting-owner.sh <<'EOS' \
    && chmod +x /docker-entrypoint-initdb.d/0004-starting-owner.sh
#!/bin/sh
set -e

if [ -z "${LEMURS_OWNER_EMAIL:-}" ]; then
  echo "LEMURS_OWNER_EMAIL not set; skipping starting owner insert." >&2
  exit 0
fi

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<SQL
INSERT INTO "authorized_email_elevated" ("email", "role", "expiration")
VALUES ('${LEMURS_OWNER_EMAIL}', '3', '3000-01-01 00:00:00.000')
ON CONFLICT (email, role) DO NOTHING;
SQL
EOS
