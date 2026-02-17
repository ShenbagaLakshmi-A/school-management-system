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

echo "[INFO] Deleting old Kubernetes deployments and services if any..."
kubectl delete deployment eureka-server hotel-gateway customer-service hotel-service reservation-service notification-service payment-service postgres --ignore-not-found
kubectl delete service eureka-server hotel-gateway customer-service hotel-service reservation-service notification-service payment-service postgres --ignore-not-found
echo "[INFO] Old Kubernetes resources cleaned up."

###############################################
# STEP 2 - Deploy PostgreSQL
###############################################

echo "[INFO] Deploying PostgreSQL..."
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml

###############################################
# STEP 3 - Wait for PostgreSQL pod to be running
###############################################

echo "[INFO] Waiting for PostgreSQL pod to be in Running state..."
POSTGRES_POD=""
while [ -z "$POSTGRES_POD" ]; do
    POSTGRES_POD=$(kubectl get pods -l app=postgres -o jsonpath="{.items[0].metadata.name}" 2>/dev/null || true)
    [ -z "$POSTGRES_POD" ] && sleep 2
done

kubectl wait --for=condition=Ready pod/$POSTGRES_POD --timeout=180s
echo "[INFO] PostgreSQL pod is running."

###############################################
# STEP 3a - Wait until PostgreSQL is fully ready
###############################################

echo "[INFO] Waiting for PostgreSQL to accept connections..."
until kubectl exec $POSTGRES_POD -- pg_isready -U postgres > /dev/null 2>&1; do
    echo "[INFO] PostgreSQL not ready yet. Sleeping 2s..."
    sleep 2
done
echo "[INFO] PostgreSQL is ready!"

###############################################
# STEP 3b - Create required databases
###############################################

echo "[INFO] Creating databases..."
kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE customer_db;" || true
kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE hotel_db;" || true
kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE reservation_db;" || true
kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE payment_db;" || true
kubectl exec $POSTGRES_POD -- psql -U postgres -c "CREATE DATABASE notification_db;" || true

echo "[INFO] Databases created successfully!"

# Print all databases to verify
echo "[INFO] Listing all databases:"
kubectl exec $POSTGRES_POD -- psql -U postgres -c "\l"

###############################################
# STEP 4 - Build Docker images (fresh for all services)
###############################################

images=("eureka-server" "hotel-gateway" "customer-service" "hotel-service" \
        "reservation-service" "notification-service" "payment-service")

for image in "${images[@]}"; do
    echo "[DEBUG] Deleting old Docker image for $image if exists..."
    docker rmi -f ${image}:latest || true

    echo "[DEBUG] Building fresh Docker image for $image..."
    docker build --no-cache -t ${image}:latest ./$image
done

###############################################
# STEP 5 - Deploy microservices to Kubernetes
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

echo "============================================="
echo " ✅ DEPLOYMENT & IMAGE BUILD COMPLETED "
echo "============================================="

EUREKA_IP=$(minikube ip)
echo "[INFO] You can access Eureka dashboard at: http://$EUREKA_IP:8761"
