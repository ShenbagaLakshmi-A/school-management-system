pipeline {
    agent any

    tools {
        maven 'Maven-3.9.4'
        jdk 'OpenJDK-17'
    }

    stages {
        stage('Prepare Workspace & Checkout') {
            steps {
                deleteDir()
                git(
                    url: 'git@github.com:ShenbagaLakshmi-A/hotel-reservation-system.git',
                    branch: 'main',
                    credentialsId: 'jenkins-container-ssh'
                )
                dir('.') {
                    sh 'git status'
                    sh 'git remote -v'
                }
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Building Maven project and running tests...'
                sh 'mvn clean package'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}