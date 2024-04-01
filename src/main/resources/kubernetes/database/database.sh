#!/bin/bash
kubectl apply -f postgres-configmap.yaml
kubectl apply -f psql-pv.yaml
kubectl apply -f psql-claim.yaml
kubectl apply -f ps-deployment.yaml
kubectl apply -f ps-service.yaml

#kubectl exec -it postgres-665b7554dc-cddgq -- psql -h localhost -U ps_user --password -p 5432 ps_db

