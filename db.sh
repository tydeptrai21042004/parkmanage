#!/bin/sh
set -e

: "${PGDATA:=/var/lib/postgresql/data}"
: "${POSTGRES_USER:=park}"
: "${POSTGRES_PASSWORD:=your_password}"
: "${POSTGRES_DB:=parkdb}"

mkdir -p "$PGDATA" /var/run/postgresql
chown -R postgres:postgres /var/lib/postgresql /var/run/postgresql

# 1) Init cluster if missing
if [ ! -s "$PGDATA/PG_VERSION" ]; then
  echo "Initializing PostgreSQL at $PGDATA..."
  su-exec postgres initdb -D "$PGDATA"
fi

# 2) Start server (bg)
echo "Starting PostgreSQL..."
su-exec postgres postgres -D "$PGDATA" -c listen_addresses='*' &
PG_PID=$!

# Wait until ready
for i in $(seq 1 30); do
  if pg_isready -h /var/run/postgresql >/dev/null 2>&1; then break; fi
  sleep 0.5
done

# 3) Ensure role exists
su-exec postgres psql -U postgres -tc "SELECT 1 FROM pg_roles WHERE rolname='${POSTGRES_USER}'" | grep -q 1 || \
  su-exec postgres psql -U postgres -c "CREATE ROLE \"${POSTGRES_USER}\" LOGIN SUPERUSER PASSWORD '${POSTGRES_PASSWORD}';"

# 4) Ensure database exists
su-exec postgres psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname='${POSTGRES_DB}'" | grep -q 1 || \
  su-exec postgres createdb -U postgres -O "${POSTGRES_USER}" "${POSTGRES_DB}"

echo "PostgreSQL ready. Executing: $*"
exec "$@"