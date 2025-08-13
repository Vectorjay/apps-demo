def gv 

pipeline {
    agent any

    // tools {
    //     maven 'maven'  // Replace with the name you've configured in Jenkins -> Global Tool Configuration
    // }
    parameters {
        choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }
    
    stages{

        stage("init") {
            steps{
                script{
                    gv = load "script.groovy"
                }
            }
        }

        stage("build") {
            steps{
                script{
                    gv.buildApp()
                }
            }
        }

        stage("test") {
            when{
                expression {
                    params.executeTests
                }
            }
            steps{
                script{
                    gv.tesApp()
                }
            }
        }

        stage("deploy") {
            input{
                message "select the environment to deploy to"
                ok "Done"
                parameters{
                    choice(name: 'ENV', choices: ['dev', 'staging', 'prod'], description: '')
                }
            }
            steps{
                script{
                    gv.deploypp()
                    echo "Deploying to ${ENV}"
                }
            }
        }

    }
    
}
