pipeline {
    agent any
    
    tools{
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }
    
    environment {
        // Snyk configuration
        SNYK_SEVERITY_THRESHOLD = 'high'
    }
    
    stages {

        stage("increment version") {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh '''
                        mvn build-helper:parse-version versions:set \
                          -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                          versions:commit
                    '''
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                    env.FULL_IMAGE_NAME = "vectorzy/demo-app:${IMAGE_NAME}"
                }
            }
        }

        stage("build app") {
            steps {
                script {
                    echo "building the application"
                    sh 'mvn clean package'  
                }
            }
        }

        stage("build image") {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t ${FULL_IMAGE_NAME} ."
                        sh "echo \$PASS | docker login -u \$USER --password-stdin"
                        sh "docker push ${FULL_IMAGE_NAME}"
                    }
                }
            }
        }

        stage("Snyk Security Scan") {
            steps {
                script {
                    echo "Scanning image with Snyk..."
                    
                    withCredentials([
                        usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER'),
                        string(credentialsId: 'snyk-auth-token', variable: 'SNYK_TOKEN')
                    ]) {
                        sh """
                            # Run Snyk scan using Docker
                            docker run --rm \\
                                -e SNYK_TOKEN=\${SNYK_TOKEN} \\
                                -v /var/run/docker.sock:/var/run/docker.sock \\
                                snyk/snyk:docker \\
                                snyk container test ${FULL_IMAGE_NAME} \\
                                --file=Dockerfile \\
                                --severity-threshold=${SNYK_SEVERITY_THRESHOLD} \\
                                --org=your-org-name \\
                                --json-file-output=/workdir/snyk-results.json
                        """
                        
                        // Generate HTML report
                        sh """
                            docker run --rm \\
                                -v \$(pwd):/workdir \\
                                -v /var/run/docker.sock:/var/run/docker.sock \\
                                -e SNYK_TOKEN=\${SNYK_TOKEN} \\
                                snyk/snyk:docker \\
                                sh -c 'snyk-to-html -i /workdir/snyk-results.json -o /workdir/snyk-report.html'
                        """
                    }
                }
            }
            
            post {
                always {
                    // Archive scan results
                    archiveArtifacts artifacts: 'snyk-results.json, snyk-report.html', allowEmptyArchive: true
                    
                    // Publish HTML report
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '',
                        reportFiles: 'snyk-report.html',
                        reportName: 'Snyk Security Report'
                    ])
                }
            }
        }

        stage("commit version update") {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "jenkins"'

                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/Vectorjay/apps-demo.git"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:refs/heads/jenkins-jobs'
                    }
                }
            }
        }

    }
    
    post {
        success {
            echo "Pipeline completed successfully!"
            
            script {
                // Clean up local docker images if we're still in agent context
                sh """
                    docker rmi ${env.FULL_IMAGE_NAME} || true
                    echo "Cleaned up Docker images"
                """
            }
        }
        failure {
            echo "Pipeline failed!"
            
            // Optional: Add notification here
            script {
                // You can add Slack/email notifications here
                echo "Build failed with status: ${currentBuild.result}"
            }
        }
        always {
            echo "Pipeline finished!"
            
            // Remove the problematic fileExists check or wrap it in script block
            script {
                // Simple cleanup or summary
                echo "Cleaning up workspace..."
                // You can add cleanup commands here if needed
            }
        }
    }
}