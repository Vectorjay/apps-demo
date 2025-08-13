def buildApp(){
    echo 'building the application...'
}

def tesApp(){
    echo 'testing the application...'
}

def deploypp(){
    echo 'deploying the application...'
    echo "deploying version ${params.VERSION}"
}

return this
