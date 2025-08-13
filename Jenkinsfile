pipeline {
    agent any

    tools {
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }

    stages {
        stage('Build app') {
            steps {
                echo 'Building app'
                sh 'mvn package'
            }
        }
    }
}
