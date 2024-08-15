def buildJar() {
    echo 'building the application...'
    sh 'mvn package'
}

def deployApp() {
    echo 'deploying the application...'
}

return this
