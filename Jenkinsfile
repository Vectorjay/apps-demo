pipeline {
    agent any

    stages {
        stage('BUild app') {
            steps {
                script{
                    sh 'mvn package'
                }
                echo 'Building app'
            }
        }
    }   
}
