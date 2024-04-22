pipeline {
    agent any

    tools {
        jdk "Java17"
        maven "Maven3"
    }

    environment {
        APP_NAME = "MinecraftKubernetesService"
        RELEASE = "1.0.0"
        DOCKER_USER = "hubirto"
        DOCKER_PASS = 'dockerhub'
        IMAGE_NAME = "${DOCKER_USER}" + "/" + "${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
    }

    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout from SCM') {
            steps {
                git branch: 'master', credentialsId: 'jenkins-github-token', url: 'https://github.com/HubiRto/MinecraftKubernetesService.git'
            }
        }
        stage('Build App') {
            steps {
                sh "mvn clean package"
            }
        }

        stage('Test App') {
            steps {
                sh "mvn test"
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token') {
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-token'

                }
            }
        }
    }
}