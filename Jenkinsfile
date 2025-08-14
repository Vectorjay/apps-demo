def gv

pipeline {
    agent any

    tools {
        maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    }
    
    stages{

        stage("init") {
            steps{
                script{
                    gv = load "script.groovy"
                }
            }
        }

        stage("increment version") {
            steps{
                script{
                    gv.increment()
                }
            }
        }

        stage("build jar") {
            steps{ 
                script{
                    gv.buildJar()
                }
            }
        }

        stage("build image") {
            steps{
                script{
                    gv.buildImage()
                }
            }
        }

        stage("deploy") {
            steps{
                script{
                    gv.deployApp()
                }
            }
        }

    }
    
}
