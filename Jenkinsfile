pipeline {
    agent any

    environment {
        GPG_TTY = '/dev/tty'  // Make sure GPG works in Jenkins
    }

    tools {
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }

    stages {
        stage('Check Tools') {
            steps {
                script {
                    sh 'which pass || echo "pass not found"'
                    sh 'which docker-credential-pass || echo "docker-credential-pass not found"'
                }
            }
        }

        stage("build jar") {
            steps {
                script {
                    echo "Building application..."
                    sh 'mvn package'
                }
            }
        }

        stage("Docker Login") {
            steps {
                script {
                    echo "Logging into Docker..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                    }
                }
            }
        }

        stage("build image") {
            steps {
                script {
                    echo "Building Docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'docker build -t vectorzy/demo-app:jma-2.0 .'
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh 'docker push vectorzy/demo-app:jma-2.0'
                    }
                }
            }
        }

        stage("deploy") {
            steps {
                script {
                    echo "Deploying the application..."
                    // You can add your deploy logic here
                }
            }
        }
    }
}
