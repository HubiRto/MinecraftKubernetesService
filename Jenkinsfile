pipeline {
    agent any

    tools {
        jdk "Java17"
        maven "Maven3"
    }

    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout from SCM') {
            steps {
                git branch: 'master', url: 'https://github.com/HubiRto/MinecraftKubernetesService.git'
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
                withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token') {
                    sh "mvn sonar:sonar"
                }
            }
        }
    }
}