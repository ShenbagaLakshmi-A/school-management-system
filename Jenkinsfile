pipeline {
    agent any

    environment {
        IMAGE_NAME = "hotel-reservation-system"
        DOCKER_REGISTRY = "your-dockerhub-username" // Optional if pushing
        K8S_NAMESPACE = "default" // Kubernetes namespace
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Code already checked out by Pipeline from SCM"
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${IMAGE_NAME}:latest")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        docker.image("${IMAGE_NAME}:latest").push()
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh """
                    kubectl set image deployment/hotel-reservation-system \
                        hotel-reservation-system=${DOCKER_REGISTRY}/${IMAGE_NAME}:latest \
                        -n ${K8S_NAMESPACE}
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed. Check console output."
        }
    }
}