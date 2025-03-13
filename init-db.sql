-- Create the clients table
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
    last_usage_date TIMESTAMP,
    account_status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    CONSTRAINT check_client_type CHECK (client_type IN ('REAL', 'LEGAL')),
    CONSTRAINT check_account_status CHECK (account_status IN ('ACTIVE', 'INACTIVE', 'BANNED'))
);

-- Create index for faster lookups
CREATE INDEX idx_national_id ON clients(national_id);
CREATE INDEX idx_account_number ON clients(account_number);

-- Create the client_change_logs table
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

-- Create index for faster lookups
CREATE INDEX idx_client_change_logs_client_id ON client_change_logs(client_id);
CREATE INDEX idx_client_change_logs_changed_at ON client_change_logs(changed_at);

-- Create a sequence for ID generation (optional, as we're using IDENTITY)
-- CREATE SEQUENCE clients_seq START WITH 1 INCREMENT BY 1;

COMMIT; 