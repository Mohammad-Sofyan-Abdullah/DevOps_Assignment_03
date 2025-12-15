pipeline {
    // Agent: Use the specified Docker image for a stable build environment
    agent {
        docker {
            image 'markhobson/maven-chrome'
            // NOTE: The 'root:root' user might cause permission issues on some hosts. 
            // If mvn test fails, try removing this arg: '-u root:root'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    // Define the collaborator emails for easy maintenance
    environment {
        // List of all collaborators to receive the final email
        COLLABORATORS = 'sofyanrajpoot567@gmail.com, qasimalik@gmail.com, irfaanyousafzai@gmail.com'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo '📥 Cloning code from GitHub...'
                // Using the specific repository you provided
                git branch: 'main', url: 'https://github.com/malik-qasim/JavaMaven.git'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running Maven tests...'
                // Assumes 'markhobson/maven-chrome' has the necessary dependencies and Maven is configured
                sh 'mvn clean test' 
            }
        }

        stage('Publish Test Results') {
            steps {
                echo '📝 Publishing JUnit test reports...'
                // Collect and publish the results using the JUnit plugin
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            echo '🧹 Post-build actions starting...'
            
            script {
                // 1. Configure git for safety
                sh "git config --global --add safe.directory ${env.WORKSPACE}"
                
                // 2. Test result parsing logic (from your reference file)
                def raw = catchError(returnStdout: true, message: "No test reports found for parsing.") {
                    sh(script: "grep -h \"<testcase\" target/surefire-reports/*.xml", returnStdout: true).trim()
                }

                int total = 0
                int passed = 0
                int failed = 0
                int skipped = 0
                def details = ""

                if (raw) {
                    raw.split('\n').each { line ->
                        total++
                        // Use regex to extract the test case name
                        def matcher = line =~ /name=\"([^\"]+)\"/
                        def name = matcher.matches() ? matcher[0][1] : "Unknown Test"

                        if (line.contains("<failure")) {
                            failed++
                            details += "❌ ${name} — FAILED\n"
                        } else if (line.contains("<skipped") || line.contains("</skipped>")) {
                            skipped++
                            details += "⏭️ ${name} — SKIPPED\n"
                        } else {
                            passed++
                            details += "✅ ${name} — PASSED\n"
                        }
                    }
                } else {
                    details = "No detailed test results found (check 'mvn test' status)."
                }

                def emailBody = """
                    Test Summary (Build #${env.BUILD_NUMBER})

                    Total Tests:    ${total}
                    Passed:         ${passed}
                    Failed:         ${failed}
                    Skipped:        ${skipped}

                    Detailed Results:
                    ${details}
                """
                
                // 3. Email sending with status confirmation
                echo '📋 Build execution completed. Attempting to send email notification to collaborators.'
                
                try {
                    emailext(
                        // Add the build status to the subject for clarity
                        subject: "Jenkins Pipeline: ${currentBuild.fullDisplayName} - ${currentBuild.currentResult} - Test Summary",
                        body: emailBody, // Using the detailed summary created above
                        to: env.COLLABORATORS,
                        // CHANGE APPLIED HERE: Setting the 'from' address as requested
                        from: 'sofyanrajpoot567@gmail.com',
                        replyTo: 'noreply@jenkins.local',
                        mimeType: 'text/plain', // Use plain text for the summary for better formatting
                        attachLog: true
                    )
                    echo '📧 **SUCCESS:** Email notification sent successfully to collaborators.'
                } catch (Exception e) {
                    echo "❌ **FAILURE:** Failed to send email notification."
                    echo "Error message: ${e.getMessage()}"
                }
            }
        }
        
        success {
            echo '✅ Pipeline completed successfully!'
        }
        
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
