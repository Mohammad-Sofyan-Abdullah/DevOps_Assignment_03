pipeline {
    // Agent: Use the specified Docker image for a stable build environment
    agent {
        docker {
            image 'markhobson/maven-chrome'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    // ERROR FIXED: The empty 'environment {}' block has been removed.

    stages {
        stage('Clone Repository') {
            steps {
                echo '📥 Cloning code from GitHub...'
                // Using the specific repository from the second Jenkinsfile for a concrete example
                // NOTE: This assumes the repository is 'https://github.com/malik-qasim/JavaMaven.git' 
                // If you are using 'https://github.com/Mohammad-Sofyan-Abdullah/DevOps_Assignment_03', change the URL below.
                git branch: 'main', url: 'https://github.com/malik-qasim/JavaMaven.git' 
            }
        }
    }

    post {
        always {
            echo '🧹 Post-build actions starting...'
            
            script {
                // Configure safe directory for git commands to avoid warnings/errors
                sh "git config --global --add safe.directory ${env.WORKSPACE}"
                
                // Get commit author email
                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae'",
                    returnStdout: true,
                    // Handle case where git log might fail or be empty gracefully
                    // by setting a fallback recipient if 'committer' is null/empty later.
                    returnStatus: true 
                )
                
                // If git log was successful, trim the output; otherwise, use a default placeholder
                def recipientEmail = committer.status == 0 ? committer.stdout.trim() : 'DEFAULT_RECIPIENTS'

                // Display build summary
                echo '📋 Build execution completed. Sending email notification.'
                
                // Send email notification using emailext
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
                    // Use the dynamically retrieved committer email, or 'DEFAULT_RECIPIENTS' fallback
                    to: recipientEmail, 
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
