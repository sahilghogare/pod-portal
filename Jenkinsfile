
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    parameters {
        string(
            name: 'TOMCAT_WEBAPPS',
            defaultValue: 'C:\\tomcat\\webapps',
            description: 'Tomcat webapps folder'
        )
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'test'],
            description: 'Target environment'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package'
                archiveArtifacts artifacts: 'target/*.war',
                                 fingerprint: true
            }
        }

        stage('Selenium Tests') {
            steps {
                bat 'mvn -B test -Pselenium -Dselenium.headless=true'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    currentBuild.description =
                        "Deployed to ${params.DEPLOY_ENV}"
                }
                echo "Deploying to ${params.DEPLOY_ENV}: ${params.TOMCAT_WEBAPPS}"
                bat "copy /Y target\\pod-portal.war \"${params.TOMCAT_WEBAPPS}\\pod-portal.war\""
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/selenium-screenshots/*.png',
                             allowEmptyArchive: true
        }
        success {
            echo 'Pipeline finished: application deployed.'
        }
        failure {
            echo 'Pipeline failed: deployment was not performed.'
        }
    }
}
