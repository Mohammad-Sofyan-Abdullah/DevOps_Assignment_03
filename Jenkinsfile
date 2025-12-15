pipeline {
    agent {
        docker {
            // Ensure this image has Maven, Chrome, and necessary dependencies for your tests
            image 'markhobson/maven-chrome'
            // Using standard Jenkin's user for volume mount for better compatibility
            args '-u 1000:1000 -v /var/lib/jenkins/.m2:/home/jenkins/.m2' 
        }
    }
    
    // Define collaborators here
    environment {
        // IMPORTANT: Replace with actual email addresses of your collaborators.
        TEAM_COLLABORATORS = 'sofyanrajpoot567@gmail.com, qasimalik@gmail.com'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo '📥 Cloning repository...'
                git branch: 'main', url: 'https://github.com/malik-qasim/JavaMaven.git'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running Maven tests...'
                // Using clean install to ensure dependencies are resolved
                sh 'mvn clean install -DskipTests=false'
            }
        }
        
        // This is necessary for the JUnit action to parse the reports
        stage('Publish Test Results') {
            steps {
                echo '📋 Publishing test results...'
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            script {
                // Ensure Jenkins has permissions to run git commands
                sh "git config --global --add safe.directory ${env.WORKSPACE}"
                
                // Get commit author email using your custom sh command
                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae'",
                    returnStdout: true
                ).trim()
                
                // --- Start of Test Report Parsing (Your Original Logic) ---
                // Check for test reports before trying to read them
                def testReportExists = fileExists('target/surefire-reports/*.xml')
                
                def emailBody = ""
                def recipientList = "${committer}, ${env.TEAM_COLLABORATORS}"

                if (testReportExists) {
                    def raw = sh(
                        script: "grep -h \"<testcase\" target/surefire-reports/*.xml",
                        returnStdout: true,
                        // Allow grep to fail if no results are found, handle empty string later
                        returnStatus: true 
                    ).trim()
                    
                    int total = 0
                    int passed = 0
                    int failed = 0
                    int skipped = 0
                    def details = ""

                    raw.split('\n').each { line ->
                        if (line.trim()) { // Ensure line is not empty
                            total++
                            def name = (line =~ /name=\"([^\"]+)\"/)[0][1]

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
                    }
                    
                    emailBody = """
                    <h2>Build ${env.BUILD_NUMBER} Test Summary</h2>
                    <p>Triggered by: <strong>${committer}</strong></p>
                    <p>Status: <strong>${currentBuild.currentResult}</strong></p>
                    <hr>
                    
<pre>
Total Tests:    ${total}
Passed:         ${passed}
Failed:         ${failed}
Skipped:        ${skipped}

Detailed Results:
${details}
</pre>
                    <p>View full report: <a href="${env.BUILD_URL}testReport/">Test Report</a></p>
                    """

                } else {
                     emailBody = """
                    <h2>Build ${env.BUILD_NUMBER} Status: ${currentBuild.currentResult}</h2>
                    <p>Triggered by: <strong>${committer}</strong></p>
                    <p><strong>Note:</strong> Test reports were not found. Check the console logs for build failures in the 'Test' stage.</p>
                    """
                }
                
                // --- End of Test Report Parsing ---

                emailext(
                    // Send to the committer (def committer) AND the collaborators (env.TEAM_COLLABORATORS)
                    to: recipientList,
                    subject: "Jenkins Pipeline: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                    body: emailBody,
                    mimeType: 'text/html' // Use HTML for a better formatted email
                )
            }
        }
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed! Check test reports.'
        }
    }
}
