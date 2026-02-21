#!/bin/bash
set -ex  # Exit on error and show commands

echo "============================================="
echo " HOTEL RESERVATION MICROSERVICES FULL RESET & DEPLOY "
echo "============================================="

###############################################
# STEP 0 - Point Docker to Minikube
###############################################
echo "[INFO] Setting Docker environment to Minikube..."
eval $(minikube docker-env)

###############################################
# STEP 1 - Delete old Kubernetes deployments/services
###############################################
echo "[INFO] Deleting old Kubernetes deployments and services..."
kubectl delete deployment eureka-server hotel-gateway customer-service hotel-service \
reservation-service notification-service payment-service postgres --ignore-not-found
kubectl delete service eureka-server hotel-gateway customer-service hotel-service \
reservation-service notification-service payment-service postgres --ignore-not-found
echo "[INFO] Old Kubernetes resources cleaned up."

###############################################
# STEP 2 - Clean Docker
###############################################
echo "[INFO] Removing old Docker images and dangling layers..."
docker system prune -af
images=("eureka-server" "hotel-gateway" "customer-service" "hotel-service" \
        "reservation-service" "notification-service" "payment-service")
for image in "${images[@]}"; do
    docker rmi -f ${image}:latest || true
done
echo "[INFO] Docker cleanup complete."

###############################################
# STEP 3 - Build fresh JARs module by module
###############################################
echo "[INFO] Building modules individually to ensure executable JARs..."
for module in "${images[@]}"; do
    echo "[INFO] Building $module..."
    cd $module
    mvn clean package -DskipTests
    cd ..
done
echo "[INFO] Maven build completed for all modules."

###############################################
# STEP 4 - Deploy PostgreSQL
###############################################
echo "[INFO] Deploying PostgreSQL..."
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml

echo "[INFO] Waiting for PostgreSQL pod to be ready..."
POSTGRES_POD=""
while [ -z "$POSTGRES_POD" ]; do
    POSTGRES_POD=$(kubectl get pods -l app=postgres -o jsonpath="{.items[0].metadata.name}" 2>/dev/null || true)
    [ -z "$POSTGRES_POD" ] && sleep 2
done
kubectl wait --for=condition=Ready pod/$POSTGRES_POD --timeout=180s

echo "[INFO] Waiting for PostgreSQL to accept connections..."
until kubectl exec $POSTGRES_POD -- pg_isready -U postgres > /dev/null 2>&1; do
    sleep 2
done
echo "[INFO] PostgreSQL is ready."

###############################################
# STEP 4a - Create required databases
###############################################
echo "[INFO] Creating required databases..."
for db in customer_db hotel_db reservation_db payment_db notification_db; do
    kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE $db;" || true
done
echo "[INFO] Databases created successfully!"
kubectl exec $POSTGRES_POD -- psql -U postgres -c "\l"

###############################################
# STEP 5 - Build fresh Docker images for all services
###############################################
for image in "${images[@]}"; do
    echo "[INFO] Building Docker image for $image..."
    docker build --no-cache -t ${image}:latest ./$image
done

###############################################
# STEP 6 - Deploy microservices to Kubernetes
###############################################
echo "[INFO] Deploying microservices..."
kubectl apply -f k8s/eureka-deployment.yaml
kubectl apply -f k8s/eureka-service.yaml

kubectl apply -f k8s/hotel-gateway-deployment.yaml
kubectl apply -f k8s/hotel-gateway-service.yaml

kubectl apply -f k8s/customer-service-deployment.yaml
kubectl apply -f k8s/customer-service.yaml

kubectl apply -f k8s/hotel-service-deployment.yaml
kubectl apply -f k8s/hotel-service.yaml

kubectl apply -f k8s/reservation-service-deployment.yaml
kubectl apply -f k8s/reservation-service.yaml

kubectl apply -f k8s/notification-service-deployment.yaml
kubectl apply -f k8s/notification-service.yaml

kubectl apply -f k8s/payment-service-deployment.yaml
kubectl apply -f k8s/payment-service.yaml

kubectl apply -f k8s/rabbitmq-deployment.yaml
kubectl apply -f k8s/rabbitmq-service.yaml

###############################################
# STEP 7 - Wait for all deployments to be available
###############################################
echo "[INFO] Waiting for all microservices to be running..."
for svc in "${images[@]}"; do
    kubectl wait --for=condition=Available deployment/$svc --timeout=180s
done
echo "[INFO] All microservices are running."

###############################################
# STEP 8 - Print service URLs
###############################################
EUREKA_IP=$(minikube ip)
echo "============================================="
echo " ✅ DEPLOYMENT COMPLETED "
echo "============================================="
echo "[INFO] Eureka dashboard: http://$EUREKA_IP:8761"
echo "[INFO] API Gateway: http://$EUREKA_IP:8080"
