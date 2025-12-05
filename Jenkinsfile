pipeline {
    agent any

    tools{
        maven 'maven'
    }
    
    environment {
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
                        string(credentialsId: 'snyk-auth-token', variable: 'SNYK_TOKEN')
                    ]) {
                        sh """
                            echo "Running Snyk scan on: ${FULL_IMAGE_NAME}"
                            echo "Current directory contents:"
                            ls -la
                            echo "Dockerfile exists: \$(test -f Dockerfile && echo 'Yes' || echo 'No')"
                            
                            # Run Snyk scan - mount current directory to access Dockerfile
                            docker run --rm \\
                                -e SNYK_TOKEN=\${SNYK_TOKEN} \\
                                -v /var/run/docker.sock:/var/run/docker.sock \\
                                -v \$(pwd):/app:ro \\
                                -w /app \\
                                snyk/snyk:docker \\
                                snyk container test ${FULL_IMAGE_NAME} \\
                                --file=/app/Dockerfile \\
                                --severity-threshold=${SNYK_SEVERITY_THRESHOLD} \\
                                --org=vectorjay \\
                                --json-file-output=/app/snyk-results.json
                        """
                        
                        # Generate HTML report
                        sh """
                            docker run --rm \\
                                -v \$(pwd):/app \\
                                -e SNYK_TOKEN=\${SNYK_TOKEN} \\
                                snyk/snyk:docker \\
                                snyk-to-html -i /app/snyk-results.json -o /app/snyk-report.html
                        """
                    }
                    
                    // Archive and publish results
                    archiveArtifacts artifacts: 'snyk-results.json, snyk-report.html', allowEmptyArchive: true
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

        stage("Cleanup and Commit") {
            steps {
                script {
                    echo "Cleaning up and committing changes..."
                    
                    // Clean up Docker image
                    sh "docker rmi ${FULL_IMAGE_NAME} || true"
                    
                    // Commit version update
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh '''
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "jenkins"
                        '''
                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/Vectorjay/apps-demo.git"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:refs/heads/jenkins-jobs'
                    }
                }
            }
        }

    }
}