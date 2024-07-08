#!/bin/bash

# Variables de entorno
DB_NAME="debtreminderdb"
DB_USER="postgres"
DB_PASS="1234"
DB_HOST="localhost"

# Comando para crear la base de datos si no existe
psql -U $DB_USER -h $DB_HOST -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | grep -q 1 || psql -U $DB_USER -h $DB_HOST -c "CREATE DATABASE $DB_NAME"

# Iniciar la aplicación Spring Boot
./mvnw spring-boot:run
