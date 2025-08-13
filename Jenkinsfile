pipeline {
    agent any

    tools {
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }
    // parameters {
    //     choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
    //     booleanParam(name: 'executeTests', defaultValue: true, description: '')
    // }
    
    stages{

        stage("build jar") {
            steps{
                script{
                    echo "building application..."
                    sh 'mvn package'
                }
            }
        }

        stage("build image") {
            steps{
                script{
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')])
                        sh 'docker build -t vectorzy/demo-app:jma-2.0 .'
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh 'docker push vectorzy/demo-app:jma-2.0'
                }
            }
        }

        stage("deploy") {
            steps{
                script{
                    echo "deploying the application..."
                }
            }
        }

    }
    
}
