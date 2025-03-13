@echo off
echo Starting Oracle database container...
docker-compose up -d

echo Waiting for Oracle database to be ready...
timeout /t 60 /nobreak

echo Initializing database schema...
docker exec -i oracle sqlplus system/oracle@//localhost:1521/ORCLCDB @%cd%\init-db.sql

echo Oracle database setup complete! 