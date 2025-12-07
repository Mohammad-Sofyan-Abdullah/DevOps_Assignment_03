pipeline {
    agent any
    
    environment {
        // Docker image names
        APP_IMAGE = 'crud-webapp'
        TEST_IMAGE = 'selenium-java-tests'
        EC2_HOST = '13.48.104.189:8000' // Your EC2 instance IP
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from GitHub...'
                checkout scm
            }
        }
        
        stage('Build Application') {
            steps {
                echo '🔨 Building Docker image for the application...'
                script {
                    // Build the FastAPI application image
                    sh 'docker-compose build web'
                }
            }
        }
        
        stage('Start Application') {
            steps {
                echo '🚀 Starting application with Docker Compose...'
                script {
                    // Start application and MongoDB
                    sh 'docker-compose up -d'
                    
                    // Wait for application to be ready
                    echo '⏳ Waiting for application to start...'
                    sh 'sleep 20'
                    
                    // Health check
                    sh '''
                        echo "🔍 Checking application health..."
                        curl -f http://localhost:8000 || exit 1
                        curl -f http://localhost:8000/api/students || exit 1
                        echo "✓ Application is running"
                    '''
                }
            }
        }
        
        stage('Build Test Image') {
            steps {
                echo '🔨 Building Selenium test Docker image...'
                script {
                    dir('selenium-tests') {
                        sh "docker build -t ${TEST_IMAGE} ."
                    }
                }
            }
        }
        
        stage('Run Selenium Tests') {
            steps {
                echo '🧪 Running Java Selenium automated tests...'
                script {
                    // Run tests in Docker container with Maven
                    sh """
                        docker run --rm \
                            --network host \
                            -v \$(pwd)/selenium-tests/target:/app/target \
                            -e BASE_URL=http://localhost:8000 \
                            ${TEST_IMAGE}
                    """
                }
            }
        }
        
        stage('Publish Test Results') {
            steps {
                echo '📊 Publishing test results...'
                script {
                    // Publish TestNG results
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'selenium-tests/target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'Selenium Test Report',
                        reportTitles: 'CRUD App Selenium Tests'
                    ])
                    
                    // Archive test results
                    archiveArtifacts artifacts: 'selenium-tests/target/surefire-reports/**/*', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up...'
            script {
                // Stop and remove containers
                sh 'docker-compose down || true'
                
                // Clean up test image
                sh "docker rmi ${TEST_IMAGE} || true"
                
                // Display test summary
                echo '📋 Test execution completed'
            }
        }
        
        success {
            echo '✅ Pipeline completed successfully!'
            echo '🎉 All Selenium tests passed!'
        }
        
        failure {
            echo '❌ Pipeline failed!'
            echo '🔍 Check test reports for details'
        }
    }
}
