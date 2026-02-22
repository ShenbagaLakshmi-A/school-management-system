pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        IMAGE_NAME = 'shenbaga/hotel-reservation-system:latest'
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Checkout the correct branch with credentials
                git(
                    url: 'git@github.com:ShenbagaLakshmi-A/hotel-reservation-system.git',
                    branch: 'main',                    // ✅ explicitly checkout 'main'
                    credentialsId: 'jenkins-container-ssh'  // ✅ make sure this exists
                )
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Building Maven project and running tests...'
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${IMAGE_NAME}"
                script {
                    docker.build("${IMAGE_NAME}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo "Pushing Docker image to Docker Hub"
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS}") {
                        docker.image("${IMAGE_NAME}").push()
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