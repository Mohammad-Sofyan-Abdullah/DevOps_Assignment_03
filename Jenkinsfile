pipeline {
    agent any
    
    // Define environment variables, including the list of recipients
    environment {
        // Docker image names
        APP_IMAGE = 'crud-webapp'
        TEST_IMAGE = 'selenium-java-tests'
        EC2_HOST = '13.48.104.189:8000' // Your EC2 instance IP
        
        // ** NEW: Define a list of team recipients/collaborators **
        // Replace these emails with your team's mailing list or individual emails
        TEAM_COLLABORATORS = 'team-alias@company.com, another-dev@email.com'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from GitHub...'
                checkout scm
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
        
        stage('Verify Application') {
            steps {
                echo '🔍 Verifying application is running...'
                script {
                    // Health check for already deployed application
                    sh '''
                        echo "🔍 Checking application health on EC2..."
                        curl -f http://13.48.104.189:8000 || exit 1
                        curl -f http://13.48.104.189:8000/api/students || exit 1
                        echo "✓ Application is running and accessible"
                    '''
                }
            }
        }
        
        stage('Run Selenium Tests') {
            steps {
                echo '🧪 Running Java Selenium automated tests...'
                script {
                    // Run tests in Docker container with Maven against EC2 deployment
                    // Tests output will be visible in console logs
                    sh """
                        docker run --rm ${TEST_IMAGE}
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up...'
            script {
                // Clean up test image
                sh "docker rmi ${TEST_IMAGE} || true"
                
                // Display test summary
                echo '📋 Test execution completed'
            }
            
            // Send email to the committer AND the predefined collaborators
            emailext(
                subject: "Jenkins Pipeline: ${currentBuild.fullDisplayName} - ${currentBuild.currentResult}",
                body: """
                    <html>
                    <body>
                        <h2>Jenkins Pipeline Build ${currentBuild.currentResult}</h2>
                        <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Build Status:</strong> ${currentBuild.currentResult}</p>
                        <p><strong>Triggered By:</strong> ${currentBuild.getBuildCauses()[0].shortDescription}</p>
                        <hr>
                        <h3>Test Results</h3>
                        <p>View detailed test report: <a href="${env.BUILD_URL}Selenium_20Test_20Report/">Selenium Test Report</a></p>
                        <p>Console Output: <a href="${env.BUILD_URL}console">View Logs</a></p>
                        <hr>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>Timestamp:</strong> ${new Date()}</p>
                    </body>
                    </html>
                """,
                // *** THE MODIFIED RECIPIENT LIST ***
                // This combines the committer(s) detected by SCM and the defined team list.
                to: '${COMMITTERS_EMAIL}, ${env.TEAM_COLLABORATORS}',
                
                from: 'jenkins@13.48.104.189',
                replyTo: 'noreply@jenkins.local',
                mimeType: 'text/html',
                attachLog: true,
                attachmentsPattern: 'selenium-tests/target/surefire-reports/**/*'
            )
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
