# Deployment Instructions

This guide describes how to deploy the Eventing Gateway solution on AWS using MSK, DynamoDB, and EKS.

## 1. Infrastructure Provisioning (Terraform)
Use the provided Terraform scripts in the `terraform/` directory to provision the AWS MSK clusters and DynamoDB table.

```bash
cd terraform
terraform init
terraform apply
```

Note: Ensure you have your AWS credentials configured.

## 2. Build the Application

### Data Plane (Kroxylicious Filter)
```bash
cd data-plane
mvn clean package
# Build and push docker image
docker build -t your-repo/data-plane:latest .
```

### Control Plane
```bash
cd control-plane
mvn clean package
# Build and push docker image
docker build -t your-repo/control-plane:latest .
```

## 3. Kubernetes Deployment
Deploy the components to your EKS cluster using the manifests in the `k8s/` directory.

### Step 3a: Deploy Configuration
Update `k8s/kroxylicious-config.yaml` with your MSK bootstrap servers and ensure you have the appropriate mTLS certificates available in the proxy's certificate path.

The configuration utilizes multiple **Virtual Clusters**:
- **Bootstrap (Port 9092)**: Entry point for clients, handles topic-based routing.
- **Backend Virtual Clusters (Ports 9093+)**: Dedicated clusters for each backend with specific mTLS settings.

```bash
kubectl apply -f k8s/kroxylicious-config.yaml
```

### Step 3b: Deploy Data Plane
```bash
kubectl apply -f k8s/data-plane.yaml
```

### Step 3c: Deploy Control Plane
```bash
kubectl apply -f k8s/control-plane.yaml
```

## 4. UI Access
Access the Control Plane UI through the LoadBalancer service created for the control plane (or via Port-Forwarding).
```bash
kubectl port-forward svc/eventing-control-plane 8080:80
```
Open `http://localhost:8080` in your browser.
