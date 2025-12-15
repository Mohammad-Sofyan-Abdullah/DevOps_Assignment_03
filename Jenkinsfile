pipeline {
    // Agent: Use the specified Docker image for a stable build environment
    agent {
        docker {
            image 'markhobson/maven-chrome'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    // Define the collaborator emails for easy maintenance
    environment {
        COLLABORATORS = 'sofyanrajpoot567@gmail.com, qasimalik@gmail.com'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo '📥 Cloning code from GitHub...'
                // Using the specific repository from the second Jenkinsfile for a concrete example
                git branch: 'main', url: 'https://github.com/malik-qasim/JavaMaven.git' 
            }
        }
    }

    post {
        always {
            echo '🧹 Post-build actions starting...'
            
            script {
                // Display build summary
                echo '📋 Build execution completed. Sending email notification to collaborators.'
                
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
                    // Use the static list of collaborators defined in the environment block
                    to: env.COLLABORATORS, 
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
