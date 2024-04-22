#!/bin/bash

K8S_DIR="/home/hubirto/Desktop/K3sServers/lobby"
DATA_DIR="$K8S_DIR/data"

# Sprawdź istnienie katalogu
if [ ! -d "$DATA_DIR" ]; then
    echo "Creating directory $DATA_DIR"
    mkdir -p "$DATA_DIR"
fi

# Aplikuj pliki YAML
kubectl apply -f "$K8S_DIR/lobby-pv-volume.yml"
kubectl apply -f "$K8S_DIR/lobby-pv-claim.yml"
kubectl apply -f "$K8S_DIR/lobby-service.yml"
kubectl apply -f "$K8S_DIR/lobby-deployment.yml"