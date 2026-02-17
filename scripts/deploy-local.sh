#!/bin/bash
# This script applies the Kubernetes manifests for the 'ordem_servico' microservice
# to a local Minikube environment using Kustomize.

# --- Determine the project root directory ---
SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)
PROJECT_ROOT=$(cd -- "$SCRIPT_DIR/../.." &> /dev/null && pwd)
SERVICE_DIR=$(cd -- "$SCRIPT_DIR/.." &> /dev/null && pwd)
# ---

# --- Install Kustomize if not present ---
if ! command -v kustomize &> /dev/null
then
    echo "Kustomize not found. Installing..."
    curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh" | bash
    sudo mv kustomize /usr/local/bin/
fi
# ---

NAMESPACE="oficina-ns"
SERVICE_NAME="ordem_servico"

echo "Applying local configuration for ${SERVICE_NAME} to Minikube..."

# Ensure the namespace exists
echo "Creating namespace ${NAMESPACE} if it doesn't exist..."
kubectl get namespace ${NAMESPACE} &> /dev/null || kubectl create namespace ${NAMESPACE}

# Build and apply the local Kustomize overlay for the service
(cd "$SERVICE_DIR/k8s/overlays/local" && kustomize build --load-restrictor=LoadRestrictionsNone . | kubectl apply -f -)

# Check for errors in the previous command
if [ $? -ne 0 ]; then
    echo "Error applying Kubernetes configuration for ${SERVICE_NAME}."
    exit 1
fi

echo ""
echo "Deploy local for ${SERVICE_NAME} concluded!"
echo "Wait a few moments for the PostgreSQL container and application to start."
echo ""
echo "To access the ${SERVICE_NAME} application, run in a new terminal:"
echo "minikube service ${SERVICE_NAME}-app-service -n ${NAMESPACE}"
