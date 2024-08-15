#!/user/bin/env groovy

library(
    identifier: 'jenkins-shared-library@master',
    retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'git@github.com:asFucuhara/jenkins-shared-library.git',
        credentialsId: 'GitHub-asfucuhara-secretekey'
    ])
)
def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('Initiation...') {
            steps {
                script {
                    echo "Executing pipeline for branch $GIT_BRANCH"
                    echo 'Loading scripts...'
                    gv = load "script.groovy"
                }
            }
        }
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
        stage('build app') {
            steps {
                script {
                    gv.buildJar() // <-- from local script
                }
            }
        }
        stage('build image') {
            steps {
                script {// <-- from shared library
                    dockerLogin()
                    buildImage 'asfucu/demo-app:${IMAGE_NAME}'
                    dockerPush 'asfucu/demo-app:${IMAGE_NAME}'
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    echo 'deploying docker image.......'
                }
            }
        }
        stage('commit version update') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: 'GitHub-asfucuhara-secretekey', keyFileVariable: 'KEY')]){
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "jenkins"'
                         
                        sh 'git remote set-url origin git@github.com:asFucuhara/Jenkins-pipeline.git'
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh "ssh-agent bash -c 'ssh-add ${KEY}';git push origin HEAD:jenkins-jobs"
                    }
                }
            }
        }
    }
}
