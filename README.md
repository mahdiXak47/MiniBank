# Mini Bank Application

A simple banking application that allows users to create client accounts with unique account numbers.

## Features

- Create client accounts with validation for required fields
- Generate unique 14-digit account numbers
- Track account creation and expiration dates
- Update client information with change logging
- Retrieve client information by ID, national ID, or account number
- Check account balance and transaction history
- List all clients

## Technologies Used

- Java 21
- Spring Boot 3.4.3
- Spring Data JPA
- Oracle Database (via Docker)
- Lombok
- Spring Validation

## API Endpoints

### Client Management

#### Create Client

- **POST** `/api/clients/create-account`
- Creates a new client account with initial balance of 0
- Request body: ClientDTO with client details
- Returns: Created client information with generated account number

#### Update Client

- **PUT** `/api/clients/update-account/{id}`
- Updates client information
- Request body: ClientUpdateDTO with updateable fields
- Required header: `X-Updated-By` (user making the update)
- Returns: Updated client information

#### Get Account Information

- **GET** `/api/clients/account/{accountNumber}`
- Retrieves detailed account information using account number
- Returns: Account information including:
  - Registration details (name, national ID, date of birth, etc.)
  - Account status
  - Account creation and expiration dates
  - Last usage date

#### Get Account Number by National ID

- **GET** `/api/clients/national-id/{nationalId}/account`
- Retrieves account number using national ID
- Returns: Account number as string

#### Get Inventory Information

- **GET** `/api/clients/inventory/{accountNumber}`
- Retrieves account balance and status
- Returns: Inventory information including:
  - Account number
  - Account holder name
  - Current balance
  - Account status
  - Last usage date
- Note: Account must be active to retrieve balance

#### Get Client by ID

- **GET** `/api/clients/get-account-{id}`
- Retrieves client information by ID
- Returns: Complete client information

#### Get All Clients

- **GET** `/api/clients/get-all-accounts`
- Retrieves list of all clients
- Returns: Array of client information

### Transfer Operations

#### Request Transfer

- **POST** `/api/transfers/request`
- Processes a transfer request (Deposit, Harvest, or Transfer)
- Request body:
  ```json
  {
    "type": "TRANSFER",
    "senderAccountNumber": "12345678901234",
    "receiverAccountNumber": "98765432109876",
    "amount": 100.0,
    "description": "Optional description"
  }
  ```
- Returns: Tracking code for the transfer
- Notes:
  - For DEPOSIT: Only `senderAccountNumber` (target account) is required
  - For HARVEST: Only `senderAccountNumber` (source account) is required
  - For TRANSFER: Both `senderAccountNumber` and `receiverAccountNumber` are required
  - A fee of 0.1% is charged for transfers between accounts
  - Accounts must be active to participate in transfers
  - Sender must have sufficient funds (amount + fee for transfers)

#### Check Transfer Status

- **GET** `/api/transfers/status/{trackingCode}`
- Retrieves the status of a transfer request
- Returns: Transfer details including:
  - Type of transfer
  - Sender and receiver accounts
  - Amount and fee
  - Status (PENDING, COMPLETED, FAILED)
  - Request and process dates
  - Error message (if failed)

### Transfer Types

1. **Deposit**

   - Increases account balance
   - No fee charged
   - No balance check required

2. **Harvest**

   - Decreases account balance
   - No fee charged
   - Requires sufficient balance

3. **Transfer**
   - Moves money between accounts
   - 0.1% fee charged to sender
   - Requires sufficient balance (amount + fee)
   - Both accounts must be active

### Transfer Validation Rules

- Account numbers must be valid and active
- Sender and receiver accounts cannot be the same
- Sender must have sufficient funds
- Amount must be greater than zero
- For transfers:
  - Both accounts must exist and be active
  - Sender must have amount + fee available
  - Fee is 0.1% of transfer amount

## Database Setup with Docker

This application uses Oracle Database running in a Docker container. Follow these steps to set up the database:

### Prerequisites

1. Docker and Docker Compose installed on your system
2. Oracle account (to pull the Oracle Docker image)

### Setup Steps

1. Log in to the Oracle Container Registry:

   ```
   docker login container-registry.oracle.com
   ```

   You'll need to use your Oracle account credentials.

2. Pull the Oracle Database image:

   ```
   docker pull container-registry.oracle.com/database/express:latest
   ```

3. Start the Oracle Database container using Docker Compose:

   ```
   docker-compose up -d
   ```

4. Wait for the database to initialize (this may take a few minutes).

5. For convenience, you can use the provided setup scripts:

   - On Linux/Mac: `./setup-oracle.sh`
   - On Windows: `setup-oracle.bat`

   These scripts will:

   - Start the Oracle container
   - Wait for the database to be ready
   - Initialize the database schema

### Manual Database Initialization

If you prefer to initialize the database manually:

1. Connect to the Oracle database:

   ```
   docker exec -it oracle sqlplus system/oracle@//localhost:1521/ORCLCDB
   ```

2. Run the SQL commands from the `init-db.sql` file.

## Oracle Database Configuration

The application is configured to connect to an Oracle database with the following settings:

- URL: `jdbc:oracle:thin:@localhost:1521:ORCLCDB`
- Username: `system`
- Password: `oracle`
- Driver: `oracle.jdbc.OracleDriver`
- Dialect: `org.hibernate.dialect.OracleDialect`

You can modify these settings in the `application.properties` file if needed.

## Database Schema

The application uses the following database schema:

```sql
CREATE TABLE clients (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    national_id VARCHAR2(255) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    client_type VARCHAR2(10) NOT NULL,
    phone_number VARCHAR2(15) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    postal_code VARCHAR2(10) NOT NULL,
    account_number VARCHAR2(14) UNIQUE,
    account_created_at TIMESTAMP,
    account_expires_at TIMESTAMP,
    account_status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    CONSTRAINT check_client_type CHECK (client_type IN ('REAL', 'LEGAL')),
    CONSTRAINT check_account_status CHECK (account_status IN ('ACTIVE', 'INACTIVE', 'BANNED'))
);

CREATE INDEX idx_national_id ON clients(national_id);
CREATE INDEX idx_account_number ON clients(account_number);

-- Change logs table
CREATE TABLE client_change_logs (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id NUMBER NOT NULL,
    client_name VARCHAR2(255) NOT NULL,
    field_name VARCHAR2(50) NOT NULL,
    old_value VARCHAR2(255),
    new_value VARCHAR2(255),
    changed_at TIMESTAMP NOT NULL,
    changed_by VARCHAR2(100),
    CONSTRAINT fk_client_change_logs_client FOREIGN KEY (client_id) REFERENCES clients(id)
);
```

## Change Logging

The application automatically logs changes to client information in the `client_change_logs` table. Each log entry includes:

- Client ID and name
- Field that was changed
- Old and new values
- Timestamp of the change
- User who made the change

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Start the Oracle database using Docker Compose: `docker-compose up -d`
4. Run the setup script to initialize the database:
   - On Linux/Mac: `./setup-oracle.sh`
   - On Windows: `setup-oracle.bat`
5. Run the application: `./mvnw spring-boot:run`
6. Access the application at `http://localhost:8080`

## Validation Rules

- Name: Required
- National ID: Required, must be unique
- Date of Birth: Required, must be in the past
- Client Type: Required, must be either REAL or LEGAL
- Phone Number: Required, must be between 10 and 15 digits
- Address: Required
- Postal Code: Required, must be between 5 and 10 digits
