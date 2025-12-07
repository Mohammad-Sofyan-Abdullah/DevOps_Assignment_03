# Student & Book Management System

A simple CRUD application with 10 operations using MongoDB, FastAPI, HTML, CSS, and JavaScript.
Includes Docker containerization and Selenium automated testing for CI/CD integration.

## Features

### 10 CRUD Operations:
1. **Create Student** - Add new students to the database
2. **Read All Students** - View all students
3. **Read Single Student** - Get individual student details
4. **Update Student** - Edit student information
5. **Delete Student** - Remove students from database
6. **Create Book** - Add new books to the library
7. **Read All Books** - View all books
8. **Read Single Book** - Get individual book details
9. **Update Book** - Edit book information
10. **Delete Book** - Remove books from database

### Additional Features:
- ✅ 12 Selenium automated test cases (headless Chrome compatible)
- ✅ Docker containerization for easy deployment
- ✅ Jenkins pipeline integration
- ✅ CI/CD ready for AWS EC2
- ✅ MongoDB persistence with Docker volumes

## Technology Stack

- **Backend**: FastAPI (Python 3.12)
- **Database**: MongoDB 7.0
- **Frontend**: HTML, CSS, JavaScript
- **Containerization**: Docker & Docker Compose
- **Testing**: Selenium WebDriver (Headless Chrome)
- **CI/CD**: Jenkins Pipeline
- **Deployment**: AWS EC2 ready

## Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed
- Git (for Jenkins integration)

### 1. Run with Docker Compose (Recommended)

```bash
# Clone the repository (or navigate to project directory)
cd "Assignment 03"

# Build and start all services (FastAPI + MongoDB)
docker-compose up -d

# View logs
docker-compose logs -f

# Access the application
# Open browser: http://localhost:8000

# Stop the application
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### 2. Run Tests in Docker

```bash
# Build test container
docker build -t crud-tests -f Dockerfile.test .

# Run Selenium tests (make sure app is running first)
docker run --rm --network host crud-tests

# Or run tests and save reports
docker run --rm \
    --network host \
    -v "$(pwd)/test-reports:/app/test-reports" \
    crud-tests
```

## Installation (Without Docker)

1. **Install Python dependencies**:
```bash
pip install -r requirements.txt
```

2. **Install MongoDB**:
   - Download and install MongoDB Community Edition from [mongodb.com](https://www.mongodb.com/try/download/community)
   - Start MongoDB service:
     - Windows: `net start MongoDB` (or start from Services)
     - Linux/Mac: `sudo systemctl start mongod`

## Running the Application

1. **Start MongoDB** (if not running):
```bash
# Windows
net start MongoDB

# Linux/Mac
sudo systemctl start mongod
```

2. **Run the FastAPI server**:
```bash
python main.py
```

Or using uvicorn directly:
```bash
uvicorn main:app --reload
```

3. **Access the application**:
   - **Main App**: http://localhost:8000
   - **API Docs**: http://localhost:8000/docs
   - **ReDoc**: http://localhost:8000/redoc

## Selenium Tests

### Install Test Dependencies

```bash
pip install -r tests/requirements.txt
```

### Run Tests Locally

```bash
# Make sure Chrome and ChromeDriver are installed
# Make sure the application is running on http://localhost:8000

python tests/test_selenium.py
```

### Test Cases Included (12 Total):

1. **Page Load Test** - Verify main page loads successfully
2. **Create Student** - Test student creation form
3. **Read All Students** - Test student list retrieval
4. **Update Student** - Test student edit functionality
5. **Delete Student** - Test student deletion
6. **Create Book** - Test book creation form
7. **Read All Books** - Test book list retrieval
8. **Update Book** - Test book edit functionality
9. **Book Availability Toggle** - Test checkbox functionality
10. **Delete Book** - Test book deletion
11. **Tab Navigation** - Test UI tab switching
12. **Form Validation** - Test required field validation

All tests use **headless Chrome** for CI/CD compatibility.

## AWS EC2 Deployment

### 1. Prepare EC2 Instance

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Logout and login again for group changes to take effect
```

### 2. Clone and Run Application

```bash
# Clone your repository
git clone <your-repo-url>
cd "Assignment 03"

# Start application
docker-compose up -d

# Configure firewall/security group to allow port 8000
```

### 3. Access Application

- Open Security Group in AWS EC2
- Add Inbound Rule: Custom TCP, Port 8000, Source: 0.0.0.0/0
- Access: `http://<your-ec2-public-ip>:8000`

## Jenkins CI/CD Pipeline

### Setup Jenkins on EC2

```bash
# Install Jenkins
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt-get update
sudo apt-get install -y jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Configure Jenkins Pipeline

1. **Create New Pipeline Job**
   - New Item → Pipeline
   - Name: "CRUD-App-Tests"

2. **Configure Pipeline**
   - Definition: Pipeline script from SCM
   - SCM: Git
   - Repository URL: `<your-github-repo>`
   - Script Path: `Jenkinsfile`

3. **GitHub Integration** (Optional)
   - Install GitHub plugin
   - Configure webhook for automatic builds
   - Add GitHub credentials

4. **Run Pipeline**
   - The Jenkinsfile will:
     - Checkout code
     - Build Docker images
     - Start application
     - Run Selenium tests
     - Generate reports
     - Clean up containers

### Pipeline Stages

1. **Checkout** - Pull code from GitHub
2. **Build Application** - Build Docker images
3. **Start Application** - Run containers
4. **Run Tests** - Execute Selenium tests in Docker
5. **Generate Test Report** - Create test reports
6. **Post Actions** - Clean up resources

## Project Structure

```
Assignment 03/
│
├── main.py                  # FastAPI backend with all endpoints
├── requirements.txt         # Python dependencies
├── Dockerfile              # Docker image for FastAPI app
├── Dockerfile.test         # Docker image for Selenium tests
├── docker-compose.yml      # Multi-container orchestration
├── .dockerignore          # Docker ignore file
├── Jenkinsfile            # Jenkins pipeline configuration
├── README.md              # This file
│
├── templates/
│   └── index.html         # Frontend HTML
│
├── static/
│   ├── style.css          # Styling
│   └── script.js          # JavaScript logic
│
└── tests/
    ├── test_selenium.py   # Selenium test cases (12 tests)
    └── requirements.txt   # Test dependencies
```

## API Endpoints

### Students
- `POST /api/students` - Create a new student
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get a specific student
- `PUT /api/students/{id}` - Update a student
- `DELETE /api/students/{id}` - Delete a student

### Books
- `POST /api/books` - Create a new book
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get a specific book
- `PUT /api/books/{id}` - Update a book
- `DELETE /api/books/{id}` - Delete a book

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URL` | `mongodb://localhost:27017` | MongoDB connection string |
| `PYTHONUNBUFFERED` | `1` | Python output buffering |

### Setting Environment Variables

```bash
# Windows PowerShell
$env:MONGODB_URL="mongodb://your-mongo-url:27017"

# Linux/Mac/Docker
export MONGODB_URL="mongodb://your-mongo-url:27017"
```

## Docker Commands Reference

```bash
# Build images
docker-compose build

# Start services in detached mode
docker-compose up -d

# View logs
docker-compose logs -f web
docker-compose logs -f mongodb

# Stop services
docker-compose stop

# Remove containers and networks
docker-compose down

# Remove containers, networks, and volumes
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build

# Run tests
docker build -t crud-tests -f Dockerfile.test .
docker run --rm --network host crud-tests

# Check running containers
docker ps

# Check container logs
docker logs crud_webapp
docker logs crud_mongodb

# Execute commands in running container
docker exec -it crud_webapp bash
docker exec -it crud_mongodb mongosh
```

## Troubleshooting

### Application won't start
```bash
# Check if ports are available
netstat -ano | findstr :8000
netstat -ano | findstr :27017

# Check container logs
docker-compose logs web
docker-compose logs mongodb
```

### Tests failing
```bash
# Make sure application is running
curl http://localhost:8000

# Check test logs
docker run --rm --network host crud-tests

# Run tests with verbose output
python tests/test_selenium.py -v
```

### MongoDB connection issues
```bash
# Check MongoDB is running
docker ps | grep mongodb

# Check MongoDB logs
docker logs crud_mongodb

# Connect to MongoDB shell
docker exec -it crud_mongodb mongosh
```

### Port already in use
```bash
# Windows - Find and kill process
netstat -ano | findstr :8000
taskkill /PID <process_id> /F

# Linux/Mac
lsof -ti:8000 | xargs kill -9
```

## Assignment Integration

This project is designed for the following assignment requirements:

### Part-I: Selenium Testing
- ✅ 12 automated test cases using Selenium
- ✅ Headless Chrome configuration
- ✅ Tests all CRUD operations
- ✅ Python implementation
- ✅ Database integration (MongoDB)

### Part-II: Jenkins Pipeline
- ✅ Jenkins pipeline with test stage
- ✅ GitHub integration
- ✅ Docker containerized tests
- ✅ Automated test execution
- ✅ AWS EC2 compatible
- ✅ CI/CD ready

## Usage

1. Navigate to the **Students** or **Books** tab
2. Fill in the form to add new records
3. View all records in the list below
4. Click **Edit** to modify a record
5. Click **Delete** to remove a record
6. Use **Refresh** to reload the data

## Notes

- MongoDB data persists in Docker volume `mongodb_data`
- Application uses port 8000 by default
- MongoDB uses port 27017 by default
- All tests run in headless Chrome mode
- Test reports are saved in `test-reports/` directory
- Selenium tests require the application to be running

## License

This is an educational project for university coursework.

## Support

For issues or questions, please check:
1. Docker logs: `docker-compose logs`
2. Application logs: `docker logs crud_webapp`
3. MongoDB logs: `docker logs crud_mongodb`
4. Test output: Review test reports in `test-reports/`
