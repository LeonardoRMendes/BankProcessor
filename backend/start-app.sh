set -e  # Exit on any error

echo "Starting Bank Processor Application..."
echo "========================================"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if a port is in use
port_in_use() {
    lsof -i:$1 >/dev/null 2>&1
}

# Function to wait for database to be ready
wait_for_database() {
    echo "Waiting for database to be ready..."
    local max_attempts=10
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker exec bankprocessor-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P 'BankProcessor123!' -C -Q "SELECT 1" >/dev/null 2>&1; then
            echo "Database is ready!"
            return 0
        fi
        echo "   Attempt $attempt/$max_attempts - Database not ready yet..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo "Database failed to start after $max_attempts attempts"
    exit 1
}

# Check prerequisites
echo "Checking prerequisites..."

if ! command_exists docker; then
    echo "Docker is not installed or not in PATH"
    exit 1
fi

if ! command_exists java; then
    echo "Java is not installed or not in PATH"
    exit 1
fi

echo "All prerequisites are met"

# Check if ports are available
echo "Checking port availability..."

if port_in_use 1433; then
    echo "⚠Port 1433 is already in use. Checking if it's our SQL Server container..."
    if docker ps | grep -q "bankprocessor-sqlserver"; then
        echo "Port 1433 is used by our SQL Server container - continuing..."
    else
        echo "Port 1433 is used by another process. Please free the port or stop the conflicting service."
        exit 1
    fi
fi

if port_in_use 8080; then
    echo "⚠Port 8080 is already in use. Please stop any service using port 8080."
    echo "You can check what's using the port with: lsof -i:8080"
    exit 1
fi

# Start SQL Server if not already running
echo "Starting SQL Server database..."
if docker ps | grep -q "bankprocessor-sqlserver"; then
    echo "SQL Server container is already running"
else
    echo "Starting SQL Server container..."
    docker compose up -d sqlserver
fi

# Wait for database to be ready
wait_for_database

# Initialize database tables
echo "🗄Initializing database tables..."
echo "Running database initialization script..."
docker compose up db-init

# Check if tables were created successfully
echo "🔍 Verifying database tables..."
TABLES=$(docker exec bankprocessor-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P 'BankProcessor123!' -C -Q "USE bankprocessor; SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';" -h-1 | grep -E "(JOB|ARQUIVO_RETORNO)" | wc -l)

if [ "$TABLES" -eq 2 ]; then
    echo "Database tables verified (JOB and ARQUIVO_RETORNO found)"
else
    echo "Expected 2 tables (JOB, ARQUIVO_RETORNO) but found $TABLES"
    echo "Available tables:"
    docker exec bankprocessor-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P 'BankProcessor123!' -C -Q "USE bankprocessor; SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';" -h-1
fi

# Build the application
echo "Building the application..."
./mvnw clean compile -q

if [ $? -ne 0 ]; then
    echo "Application build failed"
    exit 1
fi

echo "Application built successfully"

# Start the Spring Boot application
echo "Starting Spring Boot application..."
echo "Application will be available at: http://localhost:8080"
echo "You can connect to the database using:"
echo "   Host: localhost:1433"
echo "   Database: bankprocessor"
echo "   Username: sa"
echo "   Password: BankProcessor123!"
echo ""
echo "Press Ctrl+C to stop the application"
echo "========================================"

# Run the application
./mvnw spring-boot:run
