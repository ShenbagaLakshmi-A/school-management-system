pipeline {
    agent any

    tools {
        maven 'Maven-3.9.4'   // Name of Maven in Jenkins Global Tool Configuration
        jdk 'OpenJDK-17'      // Name of JDK in Jenkins Global Tool Configuration
    }

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        IMAGE_NAME = 'shenbaga/hotel-reservation-system:latest'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git(
                    url: 'git@github.com:ShenbagaLakshmi-A/hotel-reservation-system.git',
                    branch: 'main',
                    credentialsId: 'jenkins-container-ssh'
                )
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