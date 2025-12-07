# Jenkins Setup Guide for Part II - CI/CD Pipeline with Test Stage

This guide walks through setting up Jenkins on AWS EC2 to automate testing of your CRUD application.

## Prerequisites Checklist
- ✅ AWS EC2 instance running (13.48.104.189)
- ✅ Docker installed on EC2
- ✅ Application deployed on EC2
- ✅ Selenium tests created (12 test cases)
- ✅ Jenkinsfile prepared
- ⏳ GitHub repository (to be created)
- ⏳ Jenkins installed on EC2 (to be done)

---

## Step 1: Create GitHub Repository and Push Code

### 1.1 Create GitHub Repository
1. Go to https://github.com/new
2. Repository name: `crud-webapp-selenium` (or any name you prefer)
3. Description: "CRUD Web Application with MongoDB, FastAPI, and Selenium Automated Tests"
4. Keep it **Public** (easier for Jenkins)
5. **DO NOT** initialize with README (we already have files)
6. Click "Create repository"

### 1.2 Initialize Git and Push Code

Open PowerShell in your project directory and run:

```powershell
# Navigate to project root
cd "F:\Sofyan Thing Don't Delete This Ever in Your Life\My Things\University Work\7th Semester\Topic in Data Science\Assignment\Assignment 03"

# Initialize Git repository (if not already done)
git init

# Add your GitHub username and email
git config user.name "Your GitHub Username"
git config user.email "your.email@example.com"

# Add all files
git add .

# Create initial commit
git commit -m "Add CRUD webapp with Selenium tests and Jenkins pipeline"

# Add remote repository (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/crud-webapp-selenium.git

# Push to GitHub
git branch -M main
git push -u origin main
```

**Note:** Save your GitHub repository URL - you'll need it for Jenkins configuration.

---

## Step 2: Connect to EC2 Instance

Open PowerShell and SSH into your EC2 instance:

```powershell
ssh -i "DevOps_Assignment_03_Sofyan.pem" ubuntu@13.48.104.189
```

---

## Step 3: Install Java 11 on EC2

Jenkins requires Java. Install OpenJDK 11:

```bash
# Update package list
sudo apt update

# Install Java 11
sudo apt install -y openjdk-11-jdk

# Verify installation
java -version
```

Expected output: `openjdk version "11.x.x"`

---

## Step 4: Install Jenkins on EC2

### 4.1 Add Jenkins Repository

```bash
# Add Jenkins GPG key
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

# Add Jenkins repository
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update package list
sudo apt update
```

### 4.2 Install Jenkins

```bash
# Install Jenkins
sudo apt install -y jenkins

# Start Jenkins service
sudo systemctl start jenkins

# Enable Jenkins to start on boot
sudo systemctl enable jenkins

# Check Jenkins status
sudo systemctl status jenkins
```

Press `q` to exit status view. Jenkins should show as **active (running)**.

### 4.3 Get Initial Admin Password

```bash
# Get the initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

**Copy this password** - you'll need it to unlock Jenkins in the browser.

---

## Step 5: Configure AWS Security Group for Jenkins

1. Go to AWS EC2 Console → Security Groups
2. Find the security group attached to your EC2 instance
3. Click "Edit inbound rules"
4. Add new rule:
   - **Type:** Custom TCP
   - **Port range:** 8080
   - **Source:** 0.0.0.0/0 (or your IP for better security)
   - **Description:** Jenkins web interface
5. Save rules

---

## Step 6: Access Jenkins Web Interface

1. Open browser and go to: **http://13.48.104.189:8080**
2. Paste the initial admin password from Step 4.3
3. Click "Continue"

### 6.1 Install Suggested Plugins

1. Select "**Install suggested plugins**"
2. Wait for plugin installation (5-10 minutes)

### 6.2 Create First Admin User

1. Fill in the form:
   - **Username:** admin (or your preferred username)
   - **Password:** (choose a strong password)
   - **Full name:** Your Name
   - **Email:** your.email@example.com
2. Click "Save and Continue"
3. Keep the default Jenkins URL: `http://13.48.104.189:8080/`
4. Click "Save and Finish"
5. Click "Start using Jenkins"

---

## Step 7: Install Required Jenkins Plugins

### 7.1 Install Docker Pipeline Plugin

1. Go to **Dashboard** → **Manage Jenkins** → **Plugins**
2. Click on **Available plugins** tab
3. Search for: `Docker Pipeline`
4. Check the box next to "Docker Pipeline"
5. Search for: `HTML Publisher` (for test reports)
6. Check the box next to "HTML Publisher Plugin"
7. Click "Install" (bottom of page)
8. Check "Restart Jenkins when installation is complete and no jobs are running"
9. Wait for Jenkins to restart (refresh page after 30 seconds)

### 7.2 Configure Jenkins to Use Docker

SSH back into EC2 and add Jenkins user to Docker group:

```bash
# Add jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins to apply changes
sudo systemctl restart jenkins

# Verify jenkins user can run docker
sudo -u jenkins docker ps
```

If successful, you should see your running containers.

---

## Step 8: Create Jenkins Pipeline Job

### 8.1 Create New Pipeline Job

1. Go to Jenkins Dashboard
2. Click "**New Item**" (top left)
3. Enter job name: `CRUD-Webapp-Selenium-Tests`
4. Select "**Pipeline**"
5. Click "OK"

### 8.2 Configure Pipeline

#### General Settings
- Description: `Automated testing pipeline for CRUD web application using Selenium`

#### Build Triggers (Optional)
- Check "**GitHub hook trigger for GITScm polling**" (for automatic builds on push)

#### Pipeline Configuration

1. **Definition:** Select "**Pipeline script from SCM**"
2. **SCM:** Select "**Git**"
3. **Repository URL:** Enter your GitHub repository URL
   - Example: `https://github.com/YOUR_USERNAME/crud-webapp-selenium.git`
4. **Credentials:** Leave as "none" (for public repositories)
5. **Branch Specifier:** `*/main` (or `*/master` if that's your default branch)
6. **Script Path:** `Jenkinsfile` (this is the file in your repository)
7. Click "**Save**"

---

## Step 9: Run Your First Pipeline

### 9.1 Manual Build

1. On the pipeline job page, click "**Build Now**" (left sidebar)
2. A new build will appear under "Build History"
3. Click on the build number (e.g., #1)
4. Click "**Console Output**" to see real-time logs

### 9.2 Expected Pipeline Stages

You should see these stages execute:
1. ✅ **Checkout** - Fetches code from GitHub
2. ✅ **Build Application** - Builds Docker image
3. ✅ **Start Application** - Starts app with docker-compose
4. ✅ **Build Test Image** - Builds Selenium test image
5. ✅ **Run Selenium Tests** - Executes 12 test cases
6. ✅ **Publish Test Results** - Generates HTML reports

### 9.3 View Test Results

1. Go back to the build page
2. Click "**Selenium Test Report**" in the left sidebar
3. View detailed test results with pass/fail status

---

## Step 10: Configure GitHub Webhook (Optional - Auto-trigger)

To automatically trigger Jenkins builds when you push code to GitHub:

### 10.1 In Jenkins
1. Make sure "GitHub hook trigger for GITScm polling" is checked in your job configuration

### 10.2 In GitHub Repository
1. Go to your repository on GitHub
2. Click "Settings" → "Webhooks" → "Add webhook"
3. **Payload URL:** `http://13.48.104.189:8080/github-webhook/`
4. **Content type:** application/json
5. **Which events:** Select "Just the push event"
6. Check "Active"
7. Click "Add webhook"

Now, every time you push to GitHub, Jenkins will automatically run the pipeline!

---

## Verification Checklist

After setup, verify:

- [ ] Jenkins accessible at http://13.48.104.189:8080
- [ ] Pipeline job created successfully
- [ ] GitHub repository connected
- [ ] Docker available to Jenkins user
- [ ] First build completes successfully
- [ ] All 6 pipeline stages pass
- [ ] Test reports published and viewable
- [ ] 12 Selenium test cases executed
- [ ] Application cleaned up after tests (post section)

---

## Troubleshooting Common Issues

### Issue 1: Docker Permission Denied
**Symptom:** "permission denied while trying to connect to the Docker daemon socket"

**Solution:**
```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Issue 2: Port 8000 Already in Use
**Symptom:** "address already in use" during "Start Application" stage

**Solution:** Stop existing containers first
```bash
docker-compose down
```

### Issue 3: GitHub Connection Failed
**Symptom:** "Failed to connect to repository"

**Solution:** 
- Verify repository URL is correct
- Make sure repository is public OR add credentials in Jenkins
- Check internet connectivity from EC2

### Issue 4: Tests Fail to Find Elements
**Symptom:** "NoSuchElementException" in test results

**Solution:**
- Increase wait time in tests (already set to 15 seconds)
- Verify application is fully started before tests run (sleep increased to 20 seconds)
- Check application is accessible at http://localhost:8000

### Issue 5: Jenkins Slow or Unresponsive
**Symptom:** Jenkins UI loads slowly or hangs

**Solution:** Increase EC2 instance memory (t2.medium recommended) or restart Jenkins:
```bash
sudo systemctl restart jenkins
```

---

## Pipeline Architecture

```
┌─────────────┐
│   GitHub    │ ──── Push Code ────┐
│  Repository │                    │
└─────────────┘                    │
                                   ▼
┌──────────────────────────────────────────────┐
│              Jenkins Pipeline                │
│                                              │
│  1. Checkout code from GitHub                │
│  2. Build application Docker image           │
│  3. Start app + MongoDB with docker-compose  │
│  4. Build Selenium test Docker image         │
│  5. Run 12 automated test cases              │
│  6. Publish HTML test reports                │
│  7. Cleanup: Stop and remove containers      │
└──────────────────────────────────────────────┘
                    │
                    ▼
         ┌──────────────────┐
         │  Test Reports    │
         │  (HTML + XML)    │
         └──────────────────┘
```

---

## What You've Accomplished (Assignment Part II)

✅ **Written automated test cases using Selenium** (12 test cases covering all CRUD operations)

✅ **Created automation pipeline in Jenkins** with test stage (6-stage pipeline)

✅ **Configured Jenkins pipeline for containerized testing** (using markhobson/maven-chrome Docker image)

✅ **Integrated CI/CD workflow** with GitHub → Jenkins → Docker → Selenium → Reports

---

## Next Steps for Demonstration

1. **Make a code change** in your repository (e.g., add a console.log or comment)
2. **Push to GitHub:** `git add . && git commit -m "Test Jenkins pipeline" && git push`
3. **Watch Jenkins automatically trigger** (if webhook configured)
4. **Show the pipeline execution** in Jenkins UI
5. **Display test reports** with all tests passing
6. **Explain each pipeline stage** for your assignment report

---

## Commands Reference

### SSH to EC2
```bash
ssh -i "DevOps_Assignment_03_Sofyan.pem" ubuntu@13.48.104.189
```

### Jenkins Service Commands
```bash
sudo systemctl status jenkins    # Check status
sudo systemctl start jenkins     # Start Jenkins
sudo systemctl stop jenkins      # Stop Jenkins
sudo systemctl restart jenkins   # Restart Jenkins
```

### Docker Commands (on EC2)
```bash
docker ps                        # List running containers
docker-compose down              # Stop all containers
docker system prune -a           # Clean up Docker resources
sudo -u jenkins docker ps        # Test jenkins user Docker access
```

### View Jenkins Logs
```bash
sudo journalctl -u jenkins -f    # Follow Jenkins logs
```

---

## Assignment Documentation Tips

For your report, include:

1. **Screenshots:**
   - Jenkins dashboard with successful pipeline
   - Each pipeline stage execution
   - Test report showing all 12 tests passing
   - GitHub repository with Jenkinsfile

2. **Architecture Diagram:** Show GitHub → Jenkins → Docker → Tests flow

3. **Explain Each Stage:**
   - Why containerized testing is important
   - How Docker enables consistent test environments
   - Benefits of automated CI/CD pipeline

4. **Test Results:** Include TestNG HTML report screenshots showing pass/fail status

Good luck with your assignment! 🚀
