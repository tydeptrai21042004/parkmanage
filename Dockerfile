FROM eclipse-temurin:17-jdk-alpine

# Install Postgres + Maven + tools
RUN apk add --no-cache postgresql postgresql-client postgresql-contrib bash su-exec maven

# Runtime env
ENV PGDATA=/var/lib/postgresql/data \
    GOOGLE_API_KEY="AIzaSyDuone_2gRBtEXmisrB17EYTNZeb1P2Row"

# Prepare dirs
RUN mkdir -p /app "$PGDATA" /var/run/postgresql && \
    chown -R postgres:postgres /var/lib/postgresql /var/run/postgresql

WORKDIR /app
COPY . /app

# Install entrypoint script that starts Postgres first
# (Assumes you have db.sh at repo root; it must be LF line endings + executable)
RUN mkdir -p /usr/local/bin && \
    cp db.sh /usr/local/bin/with-postgres && \
    chmod +x /usr/local/bin/with-postgres

# Build your app at image build-time (skip tests for speed; remove if you need tests)
RUN mvn -f park_management/pom.xml -B -DskipTests=true -DtrimStackTrace=false --no-transfer-progress clean package

EXPOSE 8080

# Start Postgres, then run your app
ENTRYPOINT ["/usr/local/bin/with-postgres"]
CMD ["java","-jar","/app/park_management/target/park_management-1.0-SNAPSHOT.jar"]
