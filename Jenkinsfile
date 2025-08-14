pipeline {
    agent any

    tools {
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }
    
    stages{

        // stage("increment version") {
        //     steps{
        //         script{
        //             echo 'incrementing app version...'
        //             sh 'mvn build-helper:parse-version versions:set \
        //                 -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementVersion} versions:commit'
        //             def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
        //             def version = matcher[0] [1]
        //             env.IMAGE_NAME = "$version-$BUILD_NUMBER"
        //         }
        //     }
        // }

        stage("build app") {
            steps{
                script{
                    echo "building the application"
                    sh 'mvn clean package'  
                }
            }
        }

        stage("build image") {
            steps{
                script{
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        sh "docker build -t vectorzy/demo-app:jm.00 ."
                        sh "echo \$PASS | docker login -u \$USER --password-stdin"
                        sh "docker push vectorzy/demo-app:jm.00"
                    }
                }
            }
        }

        stage("deploy") {
            steps{
                script{
                    echo "deploying the application"
                }
            }
        }

    }
    
}
    }
    
}
