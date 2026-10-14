pipeline {
    agent any

    parameters {
        string(
            name: 'TOMCAT_WEBAPPS',
            defaultValue: 'C:\\tomcat\\webapps',
            description: 'Absolute path to the Tomcat webapps directory'
        )
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'test', 'prod'],
            description: 'Deployment environment'
        )
    }

    tools {
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -version'
                bat 'mvn clean test package'
            }
        }

        stage('Package') {
            steps {
                archiveArtifacts artifacts: 'target/pod-portal.war',
                                 fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying to environment: ${params.DEPLOY_ENV}"
                script {
                    if (!fileExists('target/pod-portal.war')) {
                        error 'WAR file not found: target/pod-portal.war'
                    }
                    if (!fileExists(params.TOMCAT_WEBAPPS)) {
                        error "Tomcat webapps directory does not exist: ${params.TOMCAT_WEBAPPS}"
                    }
                }
                bat 'copy /Y "target\\pod-portal.war" "%TOMCAT_WEBAPPS%\\pod-portal.war"'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Review the stage logs.'
        }
    }
}