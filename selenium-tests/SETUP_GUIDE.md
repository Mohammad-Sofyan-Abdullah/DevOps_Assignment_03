# Selenium Java Tests - Complete Setup Guide

## 📋 Part 1: Writing Selenium Test Cases (Current Phase)

### Project Structure
```
selenium-tests/
├── pom.xml                           # Maven configuration
├── testng.xml                        # TestNG test suite configuration
├── Dockerfile                        # Docker image for tests
└── src/
    └── test/
        └── java/
            └── com/
                └── crud/
                    └── tests/
                        └── CRUDWebAppTests.java   # 12 test cases
```

### ✅ 12 Test Cases Implemented

1. **test01_pageLoadsSuccessfully** - Verify main page loads
2. **test02_tabNavigation** - Test tab switching between Students and Books
3. **test03_createStudent** - Create a new student record
4. **test04_readAllStudents** - Retrieve all students
5. **test05_updateStudent** - Update existing student
6. **test06_deleteStudent** - Delete a student record
7. **test07_createBook** - Create a new book record
8. **test08_readAllBooks** - Retrieve all books
9. **test09_updateBook** - Update existing book
10. **test10_bookAvailabilityToggle** - Test availability checkbox
11. **test11_deleteBook** - Delete a book record
12. **test12_formValidation** - Verify form validation

All tests use **headless Chrome** for Jenkins/EC2 compatibility.

---

## 🚀 Part 1: Steps to Run Tests Locally (Windows)

### Prerequisites
1. **Install Java JDK 11 or higher**
   ```powershell
   # Check Java installation
   java -version
   ```
   Download from: https://adoptium.net/

2. **Install Maven**
   ```powershell
   # Check Maven installation
   mvn -version
   ```
   Download from: https://maven.apache.org/download.cgi

3. **Install Google Chrome**
   - Make sure Chrome browser is installed

### Running Tests Locally

```powershell
# Navigate to selenium-tests directory
cd "f:\Sofyan Thing Don't Delete This Ever in Your Life\My Things\University Work\7th Semester\Topic in Data Science\Assignment\Assignment 03\selenium-tests"

# Update EC2 IP in CRUDWebAppTests.java (line 32)
# Change: private String baseUrl = "http://13.48.104.189:8000";
# To your EC2 IP

# Run tests
mvn clean test

# View test reports
# Open: target/surefire-reports/index.html
```

---

## 🐳 Running Tests in Docker (Locally)

```powershell
# Navigate to selenium-tests directory
cd "selenium-tests"

# Build Docker image
docker build -t selenium-java-tests .

# Run tests in Docker
docker run --rm selenium-java-tests

# Run tests and save reports
docker run --rm -v ${PWD}/target:/app/target selenium-java-tests
```

---

## 📦 Part 2: Jenkins Setup on EC2 (For Assignment Part 2)

### Step 1: Install Jenkins on EC2

```bash
# SSH into your EC2 instance
ssh -i "DevOps_Assignment_03_Sofyan.pem" ubuntu@13.48.104.189

# Update system
sudo apt update && sudo apt upgrade -y

# Install Java
sudo apt install -y openjdk-11-jdk

# Add Jenkins repository
wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'

# Install Jenkins
sudo apt update
sudo apt install -y jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Step 2: Configure Jenkins

1. **Access Jenkins Web UI**
   - Open: `http://13.48.104.189:8080`
   - Make sure EC2 Security Group allows port 8080
   
2. **Initial Setup**
   - Paste the admin password
   - Install suggested plugins
   - Create admin user

3. **Install Required Plugins**
   - Go to: Manage Jenkins → Plugins → Available
   - Install:
     - Docker Pipeline
     - GitHub Integration
     - TestNG Results Plugin
     - HTML Publisher

### Step 3: Configure Docker Access for Jenkins

```bash
# Add Jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins
sudo systemctl restart jenkins
```

### Step 4: Create GitHub Repository

```bash
# On your local machine
cd "f:\Sofyan Thing Don't Delete This Ever in Your Life\My Things\University Work\7th Semester\Topic in Data Science\Assignment\Assignment 03"

# Initialize git (if not already)
git init

# Add files
git add .
git commit -m "Add Selenium tests and Jenkins pipeline"

# Create repository on GitHub
# Then push
git remote add origin https://github.com/YOUR_USERNAME/crud-selenium-tests.git
git branch -M main
git push -u origin main
```

### Step 5: Create Jenkins Pipeline Job

1. **Create New Item**
   - Click "New Item"
   - Name: "CRUD-Selenium-Tests"
   - Type: "Pipeline"
   - Click OK

2. **Configure Pipeline**
   - **General**: Check "GitHub project" and add your repo URL
   
   - **Build Triggers**: 
     - Check "GitHub hook trigger for GITScm polling" (optional)
   
   - **Pipeline**:
     - Definition: "Pipeline script from SCM"
     - SCM: Git
     - Repository URL: Your GitHub repo URL
     - Credentials: Add GitHub credentials if repo is private
     - Branch: `*/main`
     - Script Path: `Jenkinsfile`
   
   - Click "Save"

3. **Run Pipeline**
   - Click "Build Now"
   - Monitor the console output

### Step 6: Configure GitHub Webhook (Optional - Auto Trigger)

1. **In GitHub Repository**:
   - Go to Settings → Webhooks → Add webhook
   - Payload URL: `http://13.48.104.189:8080/github-webhook/`
   - Content type: `application/json`
   - Events: "Just the push event"
   - Click "Add webhook"

2. **Now commits will trigger builds automatically**

---

## 📝 What Happens in Jenkins Pipeline

The Jenkinsfile automates:

1. **Checkout** - Pulls code from GitHub
2. **Build Application** - Builds Docker image for FastAPI app
3. **Start Application** - Starts app with MongoDB in Docker
4. **Build Test Image** - Builds Selenium test Docker image
5. **Run Tests** - Executes all 12 Selenium tests in headless Chrome
6. **Publish Results** - Generates HTML test report
7. **Cleanup** - Stops containers and cleans up

---

## 🔍 Viewing Test Results in Jenkins

After pipeline runs:
1. Go to build page
2. Click "Selenium Test Report" on left sidebar
3. View detailed test results with pass/fail status

---

## 📊 Test Reports

### Local Testing
- **Location**: `selenium-tests/target/surefire-reports/index.html`
- Open in browser to view results

### Jenkins Testing
- Accessible from Jenkins build page
- Archived automatically
- Viewable as HTML report

---

## 🛠️ Troubleshooting

### Tests Fail Locally
```powershell
# Make sure app is running
curl http://13.48.104.189:8000

# Check Chrome is installed
google-chrome --version  # Linux
# On Windows, Chrome should be in default location

# Check ChromeDriver
# WebDriverManager handles this automatically
```

### Jenkins Pipeline Fails

```bash
# Check Docker is accessible
sudo docker ps

# Check Jenkins user permissions
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# Check application is accessible
curl http://localhost:8000
```

### Docker Issues on EC2

```bash
# Restart Docker
sudo systemctl restart docker

# Clean up containers
docker-compose down
docker system prune -a

# Rebuild
docker-compose up -d --build
```

---

## 📌 Important Notes

1. **Update EC2 IP**: Change IP in `CRUDWebAppTests.java` line 32
2. **Security Groups**: Open ports 8000 (app) and 8080 (Jenkins) in EC2
3. **Headless Mode**: All tests run in headless Chrome for CI/CD
4. **Test Order**: Tests run in priority order (1-12)
5. **Docker Network**: Uses `--network host` for container communication

---

## 🎯 Assignment Checklist

### Part 1: Selenium Tests ✅
- [x] 12+ automated test cases written in Java
- [x] Using Selenium WebDriver
- [x] Headless Chrome configuration
- [x] TestNG framework
- [x] Tests all CRUD operations
- [x] Database integration verified

### Part 2: Jenkins Pipeline (To Do)
- [ ] Jenkins installed on EC2
- [ ] GitHub repository created
- [ ] Pipeline job configured
- [ ] Tests run in Docker container
- [ ] Using markhobson/maven-chrome image
- [ ] Test reports generated
- [ ] Pipeline script working

---

## 📚 Technologies Used

- **Java 11** - Programming language
- **Maven** - Build tool
- **Selenium WebDriver 4.15.0** - Browser automation
- **TestNG** - Testing framework
- **ChromeDriver** - Chrome browser driver
- **WebDriverManager** - Automatic driver management
- **Docker** - Containerization
- **Jenkins** - CI/CD automation
- **GitHub** - Version control

---

## 🔗 Useful Commands

### Maven Commands
```bash
mvn clean test              # Run tests
mvn clean test -Dtest=CRUDWebAppTests  # Run specific test class
mvn clean test -Dtest=CRUDWebAppTests#test01_pageLoadsSuccessfully  # Run single test
```

### Docker Commands
```bash
docker build -t selenium-java-tests .          # Build image
docker run --rm selenium-java-tests            # Run tests
docker run --rm --network host selenium-java-tests  # Run with host network
```

### Git Commands
```bash
git add .
git commit -m "message"
git push origin main
```

---

## 📞 Support

If you encounter issues:
1. Check test reports in `target/surefire-reports/`
2. Review Jenkins console output
3. Check Docker logs: `docker logs <container_id>`
4. Verify EC2 security group settings
5. Ensure application is accessible

---

## ✅ Success Criteria

- All 12 tests pass ✓
- Tests run in headless Chrome ✓
- Jenkins pipeline executes successfully ✓
- Test reports generated ✓
- Dockerized test execution ✓
- GitHub integration working ✓

Good luck with your assignment! 🚀
