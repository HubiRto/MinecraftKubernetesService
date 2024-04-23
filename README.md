# CI/CD (Automation Steps)
- Github Project WebHook Trigger Jenkins
- Jenkins run First CI Pipeline 
- Build Spring Boot App
- Test Spring Boot App
- Send Spring Boot Jar to SonarQube Analysis
- Check Quality Gate from SonarQube
- Build Docker Image
- Push Docker Image to Docker Hub
- Jenkins runs Second CD Pipeline
- Update Deployment Tag from Docker Hub in deployment.yml
- Push the change Deployment File to Github
- ArgoCD sync project and run Docker Image in Kubernetes k3s
![argo](https://github.com/HubiRto/MinecraftKubernetesService/assets/57270888/11000be2-d6c1-41ba-98e5-331a4b5faf5c)