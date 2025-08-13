def buildJar(){
    echo 'building the application...'
    sh 'mvn package'
}

def buildImage(){
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]){
        sh 'docker build -t vectorzy/demo-app:jma-2.0 .'
        sh "docker login -u \$USER -p \$PASS"
        sh 'docker push vectorzy/demo-app:jma-2.0'
    }
}

def deployApp(){
    echo "deploying the application..."
    
}

return this

