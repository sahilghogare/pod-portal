
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

                archiveArtifacts(
                    artifacts: 'target/*.war',
                    fingerprint: true
                )
            }
        }

        stage('Selenium Tests') {
            steps {
                script {
                    try {
                        // Start the packaged WAR using an isolated H2 database.
                        bat '''
                            powershell -NoProfile -ExecutionPolicy Bypass -Command "$dbUrl = 'jdbc:h2:mem:seleniumdb;DB_CLOSE_DELAY=-1'; $appArgs = @('-jar','target/pod-portal.war','--server.port=8083','--server.servlet.context-path=/pod-portal',('--spring.datasource.url=' + $dbUrl)); $p = Start-Process -FilePath 'java' -ArgumentList $appArgs -PassThru -RedirectStandardOutput 'selenium-app-out.log' -RedirectStandardError 'selenium-app-err.log'; Set-Content -Path selenium-app.pid -Value $p.Id"
                        '''

                        // Wait for the temporary application to become ready.
                        bat '''
                            powershell -NoProfile -ExecutionPolicy Bypass -Command "$ready = $false; for ($i = 0; $i -lt 60; $i++) { try { $r = Invoke-WebRequest -Uri 'http://localhost:8083/pod-portal/login' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { $ready = $true; break } } catch {}; Start-Sleep -Seconds 2 }; if (-not $ready) { Get-Content selenium-app-out.log -ErrorAction SilentlyContinue; Get-Content selenium-app-err.log -ErrorAction SilentlyContinue; throw 'Test application did not become ready on port 8083' }"
                        '''

                        // Test the packaged WAR rather than the Tomcat deployment.
                        bat 'mvn -B test -Pselenium -Dselenium.headless=true -Dselenium.baseUrl=http://localhost:8083/pod-portal'
                    } finally {
                        // Stop only the process started by this stage.
                        bat '''
                            powershell -NoProfile -ExecutionPolicy Bypass -Command "if (Test-Path selenium-app.pid) { $appId = [int](Get-Content selenium-app.pid); Stop-Process -Id $appId -Force -ErrorAction SilentlyContinue; Remove-Item selenium-app.pid -Force -ErrorAction SilentlyContinue }"
                        '''
                    }
                }
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
            junit(
                allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml'
            )

            archiveArtifacts(
                artifacts: 'target/selenium-screenshots/*.png',
                allowEmptyArchive: true
            )

            archiveArtifacts(
                artifacts: 'selenium-app-out.log,selenium-app-err.log',
                allowEmptyArchive: true
            )
        }

        success {
            echo 'Pipeline finished: application deployed.'
        }

        failure {
            echo 'Pipeline failed: deployment was not performed.'
        }
    }
}
