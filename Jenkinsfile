pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Verbose Git output
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: '', timeout: 10, verbose: true]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Pjagdale4545/khetsathi.git',
                        credentialsId: 'farmmitra2'
                    ]]
                ])
            }
        }

        stage('Build') {
            steps {
                echo 'Building Pharma Mitra project...'
                sh 'mvn clean compile'
            }
        }
       
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                echo '🐳 Building Docker image...'
                sh 'docker build -t khetsathi-app:latest .'

                echo '🚀 Running containers with docker-compose...'
                sh 'docker-compose up -d'
            }
        }

        stage('Health Check') {
            steps {
                echo '🔍 Checking container health status...'
                sh 'docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"'
            }
        }
    }
    
    post {
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
