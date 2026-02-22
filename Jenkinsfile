pipeline {
    // Use a Docker agent with Maven + OpenJDK 17
    agent {
        docker {
            image 'maven:3.9.4-openjdk-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.m2:/root/.m2'
        }
    }

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credentials'             // Your Docker Hub credentials ID in Jenkins
        IMAGE_NAME = 'shenbaga/hotel-reservation-system:latest' // Docker image name
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Checkout the 'main' branch from GitHub using SSH credentials
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
                echo 'Verifying Docker CLI availability inside the agent...'
                sh 'docker --version'
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