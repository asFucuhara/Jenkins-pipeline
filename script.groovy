def buildJar() {
    echo 'building the application...'
    sh 'mvn clean package'
}

def deployApp() {
    echo 'deploying the application...'
}

return this
