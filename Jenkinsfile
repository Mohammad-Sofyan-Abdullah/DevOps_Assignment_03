pipeline {
    // 1. Agent: Changed to use the specified Docker image for build environment
    agent {
        docker {
            image 'markhobson/maven-chrome'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    // 2. Environment: Removed application-specific variables as requested stages are removed
    environment {
        // No environment variables needed for this minimal pipeline
    }

    stages {
        // 3. Stage: Changed to 'Clone Repository' using the explicit 'git' step
        stage('Clone Repository') {
            steps {
                echo '📥 Cloning code from GitHub...'
                // Using the specific repository from the second Jenkinsfile for a concrete example
                git branch: 'main', url: 'https://github.com/malik-qasim/JavaMaven.git'
            }
        }
        
        // Removed stages: 'Build Test Image', 'Verify Application', 'Run Selenium Tests'
        // The pipeline will now proceed directly to the 'post' block after this stage.
    }

    // 4. Post: Simplified to send an email based on the build status, 
    // omitting complex test result parsing from the second script as no tests are run.
    post {
        always {
            echo '🧹 Cleaning up (Minimal cleanup as no artifacts were built/pulled).'
            
            // Get commit author email (requires Git to be installed or available in the agent)
            script {
                // Configure safe directory for git commands in the workspace
                sh "git config --global --add safe.directory ${env.WORKSPACE}"
                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae'",
                    returnStdout: true
                ).trim()

                // Display build summary
                echo '📋 Build execution completed'
                
                // Send email notification
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
                            <p>Console Output: <a href="${env.BUILD_URL}console">View Logs</a></p>
                            <hr>
                            <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                            <p><strong>Timestamp:</strong> ${new Date()}</p>
                        </body>
                        </html>
                    """,
                    // Sending to the committer, assuming this email is set up
                    to: committer, 
                    from: 'jenkins@your.server',
                    replyTo: 'noreply@jenkins.local',
                    mimeType: 'text/html',
                    attachLog: true
                )
            }
        }
        
        success {
            echo '✅ Pipeline completed successfully!'
        }
        
        failure {
            echo '❌ Pipeline failed! Check logs for clone issues.'
        }
    }
}
