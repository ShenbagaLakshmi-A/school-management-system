pipeline {
    agent any

    tools {
        maven 'Maven-3.9.4'   // Make sure this exists in Global Tool Configuration
        jdk 'OpenJDK-17'      // Make sure this exists in Global Tool Configuration
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

        stage('Docker Check') {
            steps {
                echo 'Checking Docker availability...'
                sh 'docker --version'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    echo "Building Docker image: ${IMAGE_NAME}"
                    def appImage = docker.build("${IMAGE_NAME}")
                    
                    echo "Pushing Docker image to Docker Hub"
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS}") {
                        appImage.push()
                    }
                }
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