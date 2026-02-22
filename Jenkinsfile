pipeline {
    agent any  // Use the host Jenkins container/agent

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credentials'             // Your Docker Hub credentials ID in Jenkins
        IMAGE_NAME = 'shenbaga/hotel-reservation-system:latest' // Docker image name
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out code from GitHub...'
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
                echo 'Verifying Docker CLI and daemon availability...'
                sh '''
                    docker --version || { echo "Docker CLI not found"; exit 1; }
                    docker info || { echo "Cannot connect to Docker daemon"; exit 1; }
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${IMAGE_NAME}"
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                echo "Pushing Docker image to Docker Hub"
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS}") {
                        sh "docker push ${IMAGE_NAME}"
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