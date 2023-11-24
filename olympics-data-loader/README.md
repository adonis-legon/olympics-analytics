# Application for ETL Olympics data from source (web pages) to a database

## Build

- run the command:

```bash
scripts$ . build.sh
```

## Run

- provision a Postgres database instance

- connect to the database instance and create the database (e.g. CREATE DATABASE <DB_NAME>;)

- create a .env file to connect to that database using the template.env file as a template for the environment variables (e.g. dev.env)

- run the command with the forma: . run.sh <env>

```bash
scripts$ # example
scripts$ . run.sh dev
```
