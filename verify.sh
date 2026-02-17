#!/bin/bash
set -e

echo "============================================="
echo " 🔍 VERIFYING HOTEL RESERVATION MICROSERVICES "
echo "============================================="

# Step 1 - Minikube & Cluster
echo "[INFO] Checking Minikube status..."
minikube status

echo "[INFO] Checking nodes..."
kubectl get nodes

# Step 2 - Pods
echo ""
echo "[INFO] Checking all pods..."
kubectl get pods

# Step 3 - Services
echo ""
echo "[INFO] Checking all services..."
kubectl get svc

# Step 4 - Verify Eureka registration
EUREKA_POD=$(kubectl get pod -l app=eureka-server -o jsonpath="{.items[0].metadata.name}")
echo ""
echo "[INFO] Checking Eureka server logs for registered services..."
kubectl logs $EUREKA_POD | grep -i "registered" || echo "[WARN] No registered services found in logs yet."

# Step 5 - Quick health test of API Gateway
GATEWAY_SVC=$(kubectl get svc hotel-gateway -o jsonpath="{.spec.ports[0].nodePort}")
MINIKUBE_IP=$(minikube ip)
GATEWAY_URL="http://$MINIKUBE_IP:$GATEWAY_SVC"

echo ""
echo "[INFO] Testing API Gateway..."
if curl -s --max-time 5 $GATEWAY_URL >/dev/null; then
    echo "[SUCCESS] API Gateway is reachable at: $GATEWAY_URL"
else
    echo "[WARN] API Gateway not reachable yet. Try port-forward or wait a few seconds."
fi

# Step 6 - Verify databases
POSTGRES_POD=$(kubectl get pod -l app=postgres -o jsonpath="{.items[0].metadata.name}")
echo ""
echo "[INFO] Checking Postgres databases..."
kubectl exec -i $POSTGRES_POD -- psql -U postgres -l | grep -E "customer_db|hotel_db|reservation_db|payment_db|notification_db"

echo ""
echo "============================================="
echo " ✅ VERIFICATION COMPLETED "
echo "============================================="
