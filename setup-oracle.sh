#!/bin/bash

# Start the Oracle database container
echo "Starting Oracle database container..."
docker-compose up -d

# Wait for the database to be ready
echo "Waiting for Oracle database to be ready..."
sleep 60  # Adjust this time as needed

# Initialize the database schema
echo "Initializing database schema..."
docker exec -i oracle sqlplus system/oracle@//localhost:1521/ORCLCDB @$(pwd)/init-db.sql

echo "Oracle database setup complete!" 