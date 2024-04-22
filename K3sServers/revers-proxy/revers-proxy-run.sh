#!/bin/bash

K8S_DIR="/home/hubirto/Desktop/K3sServers/revers-proxy"
DATA_DIR="$K8S_DIR/data"

# Sprawdź istnienie katalogu
if [ ! -d "$DATA_DIR" ]; then
    echo "Creating directory $DATA_DIR"
    mkdir -p "$DATA_DIR"
fi

# Aplikuj pliki YAML
kubectl apply -f "$K8S_DIR/revers-proxy-pv-volume.yml"
kubectl apply -f "$K8S_DIR/revers-proxy-pv-claim.yml"
kubectl apply -f "$K8S_DIR/revers-proxy-service.yml"
kubectl apply -f "$K8S_DIR/revers-proxy-deployment.yml"